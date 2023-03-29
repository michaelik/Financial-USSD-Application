package com.decoded.ussd.services.implementation;

import com.decoded.ussd.data.Menu;
import com.decoded.ussd.data.MenuOption;
import com.decoded.ussd.data.UssdSession;
import com.decoded.ussd.services.AccountService;
import com.decoded.ussd.services.MenuService;
import com.decoded.ussd.services.SessionService;
import com.decoded.ussd.services.TransactionService;
import com.decoded.ussd.services.UssdRoutingService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UssdRoutingServiceImpl implements UssdRoutingService {
    private final MenuService menuService;
    private final SessionService sessionService;
    private final AccountService accountService;
    private final TransactionService transactionService;
    private static final String[] MENU_KEYS = {"1", "2", "3", "4"};
    @Override
    public String menuLevelRouter(String sessionId, String serviceCode, String phoneNumber, String text)
            throws IOException {
        Map<String, Menu> menus;
        UssdSession session;

        menus = menuService.loadMenus();
        session = checkAndSetSession(sessionId, serviceCode, phoneNumber, text);

        return text.length() > 0  ? getNextMenuItem(session, menus) :
                menus.get(session.getCurrentMenuLevel()).getText();
    }
    public String getNextMenuItem(UssdSession session, Map<String, Menu> menus) throws IOException {
        Menu menuLevel = menus.get(session.getCurrentMenuLevel());

        if (Integer.parseInt(session.getCurrentMenuLevel()) >= 2) {
            return processMenuOption(session, menuLevel.getMenuOptions().get(0));
        }
        return userSelection(session.getText()) && !isUserExit(session)?
                processMenuOption(session, menuLevel.getMenuOptions().get(Integer.parseInt(session.getText()) - 1)) : "CON ";
    }
    public String processMenuOption(UssdSession session, MenuOption menuOption) throws IOException {
        switch (menuOption.getType()) {
            case "level_name":
                if(!isValidName(session.getText())) throw new IOException(session.getText()+" is invalid first name");
                accountService.createAccount(session);
                updateSessionMenuLevel(session, menuOption.getNextMenuLevel());
                return getMenu(menuOption.getNextMenuLevel());
            case "level_email":
                if(!isValidEmail(session.getText())) throw new IOException(session.getText()+" is invalid email address");
                accountService.createAccount(session);
                String accountNumber = accountService.getAccountNumber(session);
                sessionService.delete(session.getId());
                return processMenuOptionResponses(menuOption, accountNumber);
            case "level_Account_number":
                if(!isValidAccountNumber(session.getText())) throw new IOException(session.getText()+" is invalid account number");
                transactionService.makeDeposit(session);
                updateSessionMenuLevel(session, menuOption.getNextMenuLevel());
                return getMenu(menuOption.getNextMenuLevel());
            case "level_d_amount":
                if(isValidAmount(session.getText())) throw new IOException(session.getText()+" is invalid amount");
                isUserNotExit(session);
                transactionService.makeDeposit(session);
                sessionService.delete(session.getId());
                return processMenuOptionResponses(menuOption, null);
            case "level_w_amount":
                if(isValidAmount(session.getText())) throw new IOException(session.getText()+" is invalid amount");
                isUserNotExit(session);
                transactionService.withdraw(session);
                sessionService.delete(session.getId());
                return processMenuOptionResponses(menuOption, null);
            case "level":
                updateSessionMenuLevel(session, menuOption.getNextMenuLevel());
                return getMenu(menuOption.getNextMenuLevel());
            case "response":
                String accountBalance = accountService.getAccountBalance(session).toString();
                return processMenuOptionResponses(menuOption, accountBalance);
            default:
                return "CON ";
        }
    }
    public String processMenuOptionResponses(MenuOption menuOption,
                                             String data) {
        String response = menuOption.getResponse();
        Map<String, String> variablesMap = new HashMap<>();
        switch (menuOption.getAction()) {
            case PROCESS_USER_ACCOUNT_BALANCE:
                variablesMap.put("account_balance", "Your Account balance is "+ data);
                break;
            case PROCESS_USER_ACCOUNT_CREATION:
                variablesMap.put("account_creation", "Hooray! Your wallet is ready. Your account " + data + " has deposit, and withdraw. With a default balance of 1000");
                break;
            case PROCESS_USER_DEPOSIT_AMOUNT:
                variablesMap.put("deposit_amount", "amount deposit successfully");
                break;
            case PROCESS_USER_WITHDRAW_AMOUNT:
                variablesMap.put("withdraw_amount", "amount withdraw successfully");
                break;
        }
        response = replaceVariable(variablesMap, response);
        return response;
    }
    public String replaceVariable(Map<String, String> variablesMap, String response) {
        return new StringSubstitutor(variablesMap).replace(response);
    }
    public void updateSessionMenuLevel(UssdSession session, String menuLevel) {
        session.setPreviousMenuLevel(session.getCurrentMenuLevel());
        session.setCurrentMenuLevel(menuLevel);
        sessionService.update(session);
    }
    public String getMenu(String menuLevel) throws IOException {
        return menuService.loadMenus().get(menuLevel).getText();
    }
    public UssdSession checkAndSetSession(String sessionId, String serviceCode, String phoneNumber, String text) {
        UssdSession session;
        session = sessionService.get(sessionId);
        if (session != null) {
            session.setText(text);
            return sessionService.update(session);
        }
        session = new UssdSession();
        session.setCurrentMenuLevel("1");
        session.setPreviousMenuLevel("1");
        session.setId(sessionId);
        session.setPhoneNumber(phoneNumber);
        session.setServiceCode(serviceCode);
        session.setText(text);

        return sessionService.createUssdSession(session);
    }
    public boolean userSelection(String text){
        return Arrays.asList(MENU_KEYS).contains(text);
    }
    private boolean isUserExit(UssdSession session) throws IOException {
        if(accountService.isUserRegistered(session.getPhoneNumber()) &&
                          Objects.equals(session.getText(), "1") &&
                          Objects.equals(session.getCurrentMenuLevel(), "1")) {
            throw new IOException("You have already registered");
        }
        return false;
    }
    private void isUserNotExit(UssdSession session) throws IOException {
        if(!accountService.isUserRegistered(session.getPhoneNumber())) {
            sessionService.delete(session.getId());
            throw new IOException("Please create an account");
        }
    }
    private boolean isValidName(String firstName){
        Pattern pattern = Pattern.compile("^[A-Za-z]{2,30}$");
        Matcher matcher = pattern.matcher(firstName);
        return matcher.matches();
    }
    private boolean isValidEmail(String email){
        Pattern pattern = Pattern.compile("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    private boolean isValidAccountNumber(String accountNumber) {
        return accountNumber.chars().allMatch(Character::isDigit) && accountNumber.length() == 10;
    }
    private boolean isValidAmount(String amount) {
        return !amount.chars().allMatch(Character::isDigit) || !(amount.length() > 0);
    }
}