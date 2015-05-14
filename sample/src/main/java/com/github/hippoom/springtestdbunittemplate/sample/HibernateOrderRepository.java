package com.github.hippoom.springtestdbunittemplate.sample;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public class HibernateOrderRepository {
    @Autowired
    private SessionFactory sessionFactory;


    public Optional<Order> findByTrackingId(String trackingId) {
        Order load = (Order) sessionFactory.getCurrentSession().byId(Order.class).load(trackingId);
        return Optional.ofNullable(load);
    }

    public void store(Order order) {
        sessionFactory.getCurrentSession().save(order);
    }
}
