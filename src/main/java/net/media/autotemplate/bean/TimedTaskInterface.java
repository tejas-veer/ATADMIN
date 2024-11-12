package net.media.autotemplate.bean;

/*
    Created by shubham-ar
    on 8/2/18 3:39 PM
*/
@FunctionalInterface
public interface TimedTaskInterface<T> {
    T task() throws Exception;
}
