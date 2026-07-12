// config/WilayaInitializer.java
package com.pfe.webapp.config;

import com.pfe.webapp.entity.Wilaya;
import com.pfe.webapp.repository.WilayaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2) // Run after MissionInitializer

public class WilayaInitializer implements CommandLineRunner {

    @Autowired
    private WilayaRepository wilayaRepository;

    @Override
    public void run(String... args) throws Exception {
        // Check if wilayas already exist
        if (wilayaRepository.count() > 0) {
            return;
        }

        // All 58 Algerian Wilayas with their codes and center coordinates
        Object[][] wilayas = {
                {1, "Adrar", 27.8745, -0.2862}, {2, "Chlef", 36.1653, 1.3318}, {3, "Laghouat", 33.8000, 2.8650},
                {4, "Oum El Bouaghi", 35.8755, 7.1135}, {5, "Batna", 35.5550, 6.1741}, {6, "Béjaïa", 36.7500, 5.0833},
                {7, "Biskra", 34.8500, 5.7333}, {8, "Béchar", 31.6167, -2.2167}, {9, "Blida", 36.4700, 2.8300},
                {10, "Bouira", 36.3749, 3.8870}, {11, "Tamanrasset", 22.7850, 5.5228}, {12, "Tébessa", 35.4000, 8.1167},
                {13, "Tlemcen", 34.8833, -1.3167}, {14, "Tiaret", 35.3667, 1.3167}, {15, "Tizi Ouzou", 36.7167, 4.0500},
                {16, "Alger", 36.7538, 3.0588}, {17, "Djelfa", 34.6667, 3.2500}, {18, "Jijel", 36.8200, 5.7700},
                {19, "Sétif", 36.1911, 5.4097}, {20, "Saïda", 34.8300, 0.1517}, {21, "Skikda", 36.8750, 6.9033},
                {22, "Sidi Bel Abbès", 35.1933, -0.6414}, {23, "Annaba", 36.9000, 7.7667}, {24, "Guelma", 36.4617, 7.4283},
                {25, "Constantine", 36.3650, 6.6147}, {26, "Médéa", 36.2675, 2.7500}, {27, "Mostaganem", 35.9333, 0.0833},
                {28, "M'Sila", 35.7058, 4.5419}, {29, "Mascara", 35.3833, 0.1333}, {30, "Ouargla", 31.9500, 5.3167},
                {31, "Oran", 35.6969, -0.6331}, {32, "El Bayadh", 33.6833, 1.0167}, {33, "Illizi", 26.4833, 8.4667},
                {34, "Bordj Bou Arréridj", 36.0730, 4.7610}, {35, "Boumerdès", 36.7667, 3.4833}, {36, "El Tarf", 36.7667, 8.3167},
                {37, "Tindouf", 27.6667, -8.1333}, {38, "Tissemsilt", 35.6000, 1.8000}, {39, "El Oued", 33.3667, 6.8667},
                {40, "Khenchela", 35.4333, 7.1333}, {41, "Souk Ahras", 36.2833, 7.9500}, {42, "Tipaza", 36.5833, 2.4333},
                {43, "Mila", 36.4500, 6.2667}, {44, "Aïn Defla", 36.2667, 1.9667}, {45, "Naâma", 33.2667, -0.3167},
                {46, "Aïn Témouchent", 35.3000, -1.1333}, {47, "Ghardaïa", 32.4833, 3.6667}, {48, "Relizane", 35.7333, 0.5500},
                {49, "Timimoun", 29.2500, 0.2333}, {50, "Bordj Badji Mokhtar", 22.1333, -3.4500}, {51, "Ouled Djellal", 34.4333, 4.9667},
                {52, "Béni Abbès", 30.1333, -2.1667}, {53, "In Salah", 27.2000, 2.4833}, {54, "In Guezzam", 19.5667, 5.7667},
                {55, "Touggourt", 33.1000, 6.0667}, {56, "Djanet", 24.5500, 9.4833}, {57, "El M'Ghair", 33.9500, 5.9167},
                {58, "El Menia", 30.5833, 2.8833}
        };

        for (Object[] wilaya : wilayas) {
            Wilaya w = new Wilaya();
            w.setNumWilaya((Integer) wilaya[0]);
            w.setNom((String) wilaya[1]);
            w.setCenterLatitude((Double) wilaya[2]);
            w.setCenterLongitude((Double) wilaya[3]);
            wilayaRepository.save(w);
        }

        System.out.println("All 58 Algerian wilayas have been added to the database with center coordinates!");
    }
}