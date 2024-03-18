package com.example.demo.repository;

import com.example.demo.model.dao.ShoppingList;
import org.springframework.data.repository.Repository;

public interface IShoppingListDataLayer extends Repository<ShoppingList, Long>
{
}
