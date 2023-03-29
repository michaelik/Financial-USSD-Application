package com.decoded.ussd.data;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
@Entity
@Table(name = "tbl_user_account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "account_id")
    Integer id;
    @Column(name = "firstname", columnDefinition = "VARCHAR(255)")
    String firstname;
    @Column(name = "lastname", columnDefinition = "VARCHAR(255)")
    String lastname;
    @Column(name = "email", columnDefinition = "VARCHAR(255)")
    String email;
    @Column(name = "phone_number", columnDefinition = "VARCHAR(255)")
    String phoneNumber;
    @Column(name = "account_number", unique = true)
    String accountNumber;
    @Builder.Default
    @Column(name = "account_balance", precision = 19, scale = 2)
    BigDecimal accountBalance = BigDecimal.valueOf(1000.0);
    @OneToOne(targetEntity = Transaction.class, cascade = CascadeType.ALL)
    Transaction transaction;
    @Builder.Default
    @CreationTimestamp
    @Column(name = "createdAt", columnDefinition = "TIMESTAMP NOT NULL")
    LocalDateTime timestamp = LocalDateTime.now();
}
