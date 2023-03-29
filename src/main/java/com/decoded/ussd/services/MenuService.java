package com.decoded.ussd.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.decoded.ussd.data.Menu;

public interface MenuService {
    String readFromInputStream(InputStream inputStream) throws IOException;
    Map<String, Menu> loadMenus() throws IOException;

}
