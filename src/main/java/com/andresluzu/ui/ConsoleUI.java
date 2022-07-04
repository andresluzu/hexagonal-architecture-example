package com.andresluzu.ui;

import com.andresluzu.application.ImmuneStore;
import com.andresluzu.domain.Antibody;
import com.andresluzu.domain.Antigen;
import com.andresluzu.infraestructure.MemoryImmuneStore;
import com.andresluzu.application.ImmuneService;
import com.andresluzu.application.ImmuneServiceImpl;
import com.andresluzu.domain.InvalidAntigenException;
import com.andresluzu.domain.LymphaticSystem;

import java.util.Scanner;

public class ConsoleUI {
    static ImmuneStore store = new MemoryImmuneStore();
    static ImmuneService service = new ImmuneServiceImpl(store);
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        final String messageFormat = "\nEnter antigen value (%d to %d) or '0' to exit: ";
        int value = 0;
        do {
            System.out.print(String.format(messageFormat, 1, LymphaticSystem.MAX_ANTIGEN_VALUE - 1));
            try {
                value = scanner.nextInt();
                Antibody antibody = service.respond(new Antigen(value));
                System.out.println(antibody);
            } catch (InvalidAntigenException e) {
                System.out.println("Invalid antigen value: " + e.getValue());
            } catch (Exception e) {
                System.out.println("Antigen value must be an integer");
            }
        } while (value != 0);
    }
}
