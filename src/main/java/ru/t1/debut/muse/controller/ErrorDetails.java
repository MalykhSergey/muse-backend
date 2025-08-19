package ru.t1.debut.muse.controller;

public record ErrorDetails<T>(String message, T details) {
}
