package com.example.springbatchinflearn.batchExecution.chunk.reader;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class Address {

    @Id
    @GeneratedValue
    private Long id;
    private String location;

    @OneToOne
    @JoinColumn(name = "customer3_id")
    private Customer3 customer3;

}
