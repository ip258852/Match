package org.match.controller;

import org.match.model.req.AddOrderReq;
import org.match.sevice.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class AddOrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/Order")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addOrder(@Valid @RequestBody AddOrderReq req){
        orderService.addOrder(req);
    }
}
