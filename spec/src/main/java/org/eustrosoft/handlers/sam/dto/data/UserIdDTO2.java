package org.eustrosoft.handlers.sam.dto.data;

import org.eustrosoft.handlers.JsonIgnore;
import org.eustrosoft.handlers.cms.dto.Stub;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserIdDTO2 extends UserIdDTO {
    private String newString;
    @JsonIgnore
    private Long longVal = 2L;
    @JsonIgnore
    private Short shortVal = 1;
    private Integer intVal = 3;
    private BigDecimal moneyVal = new BigDecimal(3123131.1231313212313132131231332);
    private Double doubleValue = 2.543553d;
    private Float floatVal = 3.222222f;
    private Boolean booleanVal = true;
    private List<String> listVal = new ArrayList<>();
    private List<Object> listUserDtos = new ArrayList<>();
    private Set<String> setVal = new HashSet<>();

    public UserIdDTO2(String id) {
        super(id);
        listVal.add("asd");
        listVal.add("asd1");
        listVal.add("asd2");

        listUserDtos.add(new UserIdDTO("123123"));
        listUserDtos.add(new Stub());
    }

    public String getNewString() {
        return newString;
    }

    public void setNewString(String newString) {
        this.newString = newString;
    }

}
