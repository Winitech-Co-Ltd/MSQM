package com.peru.ncov2019.ml.push.service;

/**
 * Clase para cálculo de distancia
 *
 */
public class DistanceCalculator {

	public static void main(String[] args) {	
	}
	
	/**
     * Función para calcular la distancia entre dos puntos
     *
     * @param lat1 Latitud 1
     * @param lon1 Longitud 1
     * @param lat2 Latitud 2
     * @param lon2 Longitud 2
     * @param unit Unidad de distancia visualizada
     * @return double
     */
    public static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
         
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
         
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
         
        if (unit == "kilometer") {
            dist = dist * 1.609344;
        } else if(unit == "meter"){
            dist = dist * 1609.344;
        }
 
        return (dist);
    }
     
 
    // This function converts decimal degrees to radians
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
     
    // This function converts radians to decimal degrees
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

}
