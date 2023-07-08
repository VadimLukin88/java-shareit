package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    // TODO переписать на JPQL
    List<Item> findItemByNameOrDescriptionContainsAllIgnoreCaseAndAvailableIsTrue(String searchName, String searchDesc);

    // TODO переписать на UserId
    List<Item> findItemByOwnerOrderById(User owner);

    List<Item> findByRequest_Id(Long requestId);

}
