package com.example.springbatchinflearn.batchExecution.chunk;

import org.springframework.batch.item.*;

import java.util.List;

public class CustomItemStreamReader implements ItemStreamReader<String> {

    private final List<String> items;
    private int index = -1;
    private boolean restart = false;

    public CustomItemStreamReader(List<String> items) {
        this.items = items;
        this.index = 0;
    }

    @Override
    public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

        String item = null;

        if(this.index < this.items.size()) {
            item = this.items.get(index);
            System.out.println("ItemStreamReader read " + item);
            index++;
        }

        if(this.index == 6 && !restart) {
            throw new RuntimeException("Restart is required");
        }

        return item;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        System.out.println("ItemStreamReader open");
        if(executionContext.containsKey("index")) { // 실패후 재 실행 시 index값이 존재하여 실패한 index부터 시작
            index = executionContext.getInt("index");
            this.restart = true; // 실패후 재실행시에는 RuntimeException 발생 안하도록 처리
        } else {
            index = 0; // 첫 실행 시 StepExecutionContext에 인덱스 값 저장
            executionContext.put("index", index);
        }
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        System.out.println("ItemStreamReader update");
        executionContext.put("index", index); // chunk size 만큼 읽어온후 StepExecutionContext의 index값 갱신
    }

    @Override
    public void close() throws ItemStreamException {
        System.out.println("ItemStreamReader close");
    }
}
