package com.decoded.ussd.data;

import com.decoded.ussd.enums.TransactionType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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
@Table(name = "tbl_transaction_history")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "transaction_id")
    Integer transactionId;
    @Column(name = "amount")
    BigDecimal amount;
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type")
    TransactionType transactionType;
    @ToString.Exclude
    @OneToOne(targetEntity = Account.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_account_id", referencedColumnName = "account_id")
    Account account;
    @Builder.Default
    @CreationTimestamp
    @Column(name = "createdAt", columnDefinition = "TIMESTAMP NOT NULL")
    LocalDateTime timestamp = LocalDateTime.now();
}
