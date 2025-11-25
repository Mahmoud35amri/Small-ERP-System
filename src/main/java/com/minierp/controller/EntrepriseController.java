package com.minierp.controller;

import com.minierp.model.Entreprise;
import java.util.ArrayList;
import java.util.List;

public class EntrepriseController {
    private static EntrepriseController instance;
    private List<Entreprise> entreprises = new ArrayList<>();
    private int nextId = 1;

    private EntrepriseController() {
    }

    public static synchronized EntrepriseController getInstance() {
        if (instance == null) {
            instance = new EntrepriseController();
        }
        return instance;
    }

    public List<Entreprise> getAll() {
        return new ArrayList<>(entreprises);
    }

    public void create(Entreprise e) {
        e.setId(nextId++);
        entreprises.add(e);
    }

    public void update(Entreprise e) {
        for (int i = 0; i < entreprises.size(); i++) {
            if (entreprises.get(i).getId() == e.getId()) {
                entreprises.set(i, e);
                return;
            }
        }
    }

    public void delete(Entreprise e) {
        entreprises.removeIf(ent -> ent.getId() == e.getId());
    }
}
