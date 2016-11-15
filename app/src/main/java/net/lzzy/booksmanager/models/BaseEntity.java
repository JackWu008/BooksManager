package net.lzzy.booksmanager.models;

import java.util.UUID;

/**
 * Created by Administrator on 2016/9/14.
 */
public class BaseEntity  {
    protected UUID id;
    protected String name;

    protected BaseEntity() {
        id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
