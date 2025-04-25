package com.example.miniredis.persistence;

import com.example.miniredis.datastore.DataStore;

import java.io.*;
import java.util.concurrent.*;

public class PersistenceManager {
    private final DataStore dataStore;
    private final File snapshotFile = new File("miniredis_snapshot.dat");

    public PersistenceManager(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public void saveSnapshot() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(snapshotFile))) {
            oos.writeObject(dataStore.getStore());
            oos.writeObject(dataStore.getExpireTimes());
            System.out.println("[Persistence] Snapshot saved.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadSnapshot() {
        if (!snapshotFile.exists()) {
            System.out.println("[Persistence] No snapshot found.");
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(snapshotFile))) {
            dataStore.setStore((ConcurrentHashMap<String, Object>) ois.readObject());
            dataStore.setExpireTimes((ConcurrentHashMap<String, Long>) ois.readObject());
            System.out.println("[Persistence] Snapshot loaded.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void startAutoSave() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::saveSnapshot, 60, 60, TimeUnit.SECONDS);
    }
}
