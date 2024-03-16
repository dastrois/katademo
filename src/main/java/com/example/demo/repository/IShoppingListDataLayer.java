package com.example.demo.repository;

import com.example.demo.model.dao.ShoppingList;
import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Component;

public interface IShoppingListDataLayer extends Repository<ShoppingList, Long>
{
//    void updateShoppingList(ShoppingList consumerShoppingList);
}
