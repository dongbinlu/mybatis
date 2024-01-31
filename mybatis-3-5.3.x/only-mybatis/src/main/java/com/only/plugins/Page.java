package com.only.plugins;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Page implements Serializable {

    private int page;

    private int size;

    public int getBegin() {
        return size * (page - 1);
    }

    public int getEnd() {
        return size * page;
    }

}
