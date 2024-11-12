package com.procuone.mit_kdt.service;

import com.procuone.mit_kdt.entity.BOM.Item;

import java.util.List;
import java.util.Optional;

public interface ItemService {
    Item saveItem(Item item);
    Optional<Item> getItemById(Long id);
    List<Item> getAllItems();
}
