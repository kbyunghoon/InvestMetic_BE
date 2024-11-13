package com.investmetic.domain.strategy.dto.request;

import com.investmetic.domain.strategy.model.entity.StockType;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
public class StockTypeRequestDTO {
    private String stockTypeName; // 종목명

    @Column(length = 1000)
    private String stockTypeIconURL; // 종목아이콘 경로

    private int size;



    @Builder
    public StockTypeRequestDTO(String stockTypeName, String stockTypeIconURL, int size){
        this.stockTypeName = stockTypeName;
        this.stockTypeIconURL = stockTypeIconURL;
        this.size = size;
    }
    public StockType toEntity(){
        return StockType.builder()
                .stockTypeName(stockTypeName)
                .activateState(true)
                .stockTypeIconURL(stockTypeIconURL).build();
    }

    @Override
    public String toString() {
        return "StockTypeRequestDTO{" +
                "stockTypeName='" + stockTypeName + '\'' +
                ", size=" + size +
                ", stockTypeIconURL='" + stockTypeIconURL + '\'' +
                '}';
    }
}
