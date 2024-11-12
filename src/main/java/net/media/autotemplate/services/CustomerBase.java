package net.media.autotemplate.services;


import net.media.autotemplate.bean.Customer;
import net.media.autotemplate.constants.ConfigConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerBase {
    private static List<Customer> customers = new ArrayList<>();
    private static HashMap<String, String> idMap;
    private static Customer global = new Customer();

    static {
        global.setName(ConfigConstants.GLOBAL_ENTITY);
        global.setCustomer_id("");
        global.setSearch_key(ConfigConstants.GLOBAL_ENTITY);
    }

    public static void update(List<Customer> list) {
        list.add(global);
        customers = list;
        idMap = new HashMap<>();
        for (Customer customer : customers) {
            idMap.put(customer.getCustomer_id(), customer.getName());
        }
    }

    public static List<Customer> search(String substring) {
        final String finalStr = substring.toUpperCase();
        return customers.stream().filter(item -> item.getSearch_key().contains(finalStr)).collect(Collectors.toList());
    }

    public static String getCustomerName(String id) {
        return idMap.get(id);
    }

    public static boolean contains(String entity) {
        return idMap.containsKey(entity);
    }
}
