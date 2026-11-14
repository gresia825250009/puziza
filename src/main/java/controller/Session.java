package main.java.controller;

import main.java.model.Cashier;

/** Menyimpan kasir yang terakhir login (dipakai sebagai pilihan awal di halaman order). */
final class Session {
    static Cashier currentCashier;
    private Session() {}
}
