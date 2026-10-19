package com.example.demo.service;

import com.opencagedata.jopencage.JOpenCageGeocoder;
import com.opencagedata.jopencage.model.JOpenCageForwardRequest;
import com.opencagedata.jopencage.model.JOpenCageLatLng;
import com.opencagedata.jopencage.model.JOpenCageResponse;
import com.opencagedata.jopencage.model.JOpenCageReverseRequest;
import java.util.Map;

public class LocalisationService {
    private static final String APIKEY = "11ab2c8baf8b4a8aa8fd609e78aceac1"; //2500 requêtes/jour
    private static final JOpenCageGeocoder jOpenCageGeocoder = new JOpenCageGeocoder(APIKEY);

    /**
     * Convertit des coordonnées (latitude/longitude) en une adresse
     * @param lat,lon latitude et longitude
     * @return Un L'adresse
     */
    public static String getLocalisationWithCoordinate(double lat, double lon)throws Exception{
        try {
            JOpenCageReverseRequest request = new JOpenCageReverseRequest(lat, lon);
            request.setLanguage("fr");
            JOpenCageResponse response = jOpenCageGeocoder.reverse(request);
            return response.getResults().getFirst().getFormatted();
        }catch (Exception e){
            throw new Exception("erreur"+ e.getMessage());
        }
    }
    /**
     * Convertit une adresse en coordonnées (latitude/longitude)
     * @param adresse L'adresse à géocoder
     * @return Un objet contenant la latitude et la longitude
     */

    public static Map<String, String> getCoordinateWithLocalisation(String adresse)throws Exception{
        Map<String, String> coordinate;
        try {
            JOpenCageForwardRequest request = new JOpenCageForwardRequest(adresse);
            request.setLanguage("fr");
            JOpenCageResponse response = jOpenCageGeocoder.forward(request);
            JOpenCageLatLng firstResultLatLng = response.getFirstPosition();
            coordinate = Map.of("lat", String.valueOf(firstResultLatLng.getLat()), "lon", String.valueOf(firstResultLatLng.getLng()));
            return coordinate;
        }
        catch (Exception e){
             throw new Exception("erreur"+ e.getMessage());
        }
    }
}
