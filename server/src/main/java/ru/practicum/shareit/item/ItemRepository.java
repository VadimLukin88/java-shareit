package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findItemByNameOrDescriptionContainsAllIgnoreCaseAndAvailableIsTrue(String searchName, String searchDesc);

    List<Item> findItemByOwnerOrderById(User owner);

    List<Item> findByRequest_Id(Long requestId);

    List<Item> findByRequest_IdIn(List<Long> requestIds);

}
