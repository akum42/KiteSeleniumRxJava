package com;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DataController {
  @Autowired private StockRepository repository;

  @RequestMapping("/data/{stockName}")
  public Stock greeting(@PathVariable String stockName) {
    return repository.findByStockName(stockName);
  }
}
