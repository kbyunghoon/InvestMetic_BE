package com.investmetic.global.util.stibee.dto.object;

import java.util.List;
import lombok.Data;

@Data
public class DeleteValue {
    private List<String> fail;
    private List<String> success;
}
