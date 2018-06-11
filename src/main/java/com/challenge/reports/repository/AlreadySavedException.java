package com.challenge.reports.repository;

public class AlreadySavedException extends RuntimeException {
    public AlreadySavedException(String s) {
        super(s);
    }
}
