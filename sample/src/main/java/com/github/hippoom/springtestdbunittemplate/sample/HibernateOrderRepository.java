package com.github.hippoom.springtestdbunittemplate.sample;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class HibernateOrderRepository {
    @Autowired
    private SessionFactory sessionFactory;


    public Order findByTrackingId(String trackingId) {
        Order load = (Order) sessionFactory.getCurrentSession().byId(Order.class).load(trackingId);
        return load;
    }

    public void store(Order order) {
        sessionFactory.getCurrentSession().save(order);
    }
}
