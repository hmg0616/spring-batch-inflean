package com.example.springbatchinflearn.batchExecution.chunk.writer;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Customer5 {
    /*
     * @GeneratedValue를 설정하여 자동 생성하겠다고 선언해놓고,
     * id 값을 직접 세팅후 persist 호출 시 JPA는 해당 객체가 detached 상태의 객체라 생각한다.
     * 만약 이 상태로 persist() 호출 시 “detached entity passed to persist” 에러가 나므로
     * @GeneratedValue 설정을 하지않고 직접 id값 세팅.
     * (JpaConfiguration.java 에서 JpaItemWriterBuilder<~>().usePersist(true)설정 하므로)
     */
    @Id
//    @GeneratedValue
    private Long id;
    private String firstname;
    private String lastname;
    private String birthdate;
}
