package com.pismo.trasactions.domain.account;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "accounts")
@Getter
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(name = "document_number", nullable = false, unique = true, length = 20)
    private String documentNumber;

    protected Account() {
    }

    public Account(String documentNumber) {
        this.documentNumber = documentNumber;
    }
}
