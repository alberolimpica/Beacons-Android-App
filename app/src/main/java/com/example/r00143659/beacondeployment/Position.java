package com.example.r00143659.beacondeployment;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by saraa on 02/09/2017.
 */

/*
 * The purpose of this program is to solve where an item is located given 3 distances.
 * This is solved by using the equation of a circle whose centre is at (h,k)
 *  (x - h)^2  +  (y - k)^2  =  r^2
 *   Where, as we stated (h , k) are the centre of the circle and r is the radius, or the distance of the
 *   item to the centre.
 *   For to know the exact position, we sill need to know the distance of the item from 3 different points.
 *   By triangulating them, we will solve the position
 * */

public class Position {

    public static LatLng getCenter(List<RealmBeacon> realmBeacons) {

        double[] x = new double[3];
        double[] y = new double[3];
        double[] r = new double[3];


	/*@Method beacons
	 * This method ask the user for the information about where the beacon is and the distance
	 * the item is from that beacon. Since we are doing a triangulation, it will repeat 3 times
	 * one for each beacon. Those values will be stored in an Array/matrix
	 *
	 * */
        for(int i=0; i<3; i++){
            //Latitude
            x[i] = realmBeacons.get(i).getLatitude();
            //Longitude
            y[i] = realmBeacons.get(i).getLongitude();
            //Distance
            r[i] = realmBeacons.get(i).getDistance()*(Math.pow(10,-3.0)/6371);
            Log.d("sss", "Latitude "+x[i]+ " and Longitude "+y[i]+" distance "+r[i]);
        }


    /* To solves the equation of the circle whose centre in not at the origin.
     * given 3 distances from 3 different points:
     * -> B1 = (x - a)^2 + (y - b)^2 = c^2
     * 	  B1 = x^2 + y^2 + Ax + Bx + C = 0, where A =2a,  B =2b,  C = a^2 + b^2 - c^2
     * -> B2 = (x - d)^2 + (y - e)^2 = f^2
     * 	  B2 = x^2 + y^2 + Dx + Ex + F = 0, where D =2d,  E =2e,  F = d^2 + e^2 - f^2
     * -> B3 = (x - a)^2 + (y - b)^2 = c^2
     *    B3 = x^2 + y^2 + Gx + Hx + I = 0, where G =2g,  H =2h,  I = g^2 + h^2 - i^2
     *
     *    If you subtract two of the three equations, you will get the equation of the
     *    line passing through the two points of intersection of both circles.
     *    Repeat this with any other two equations, to get a second equation of the line.
     *    The intersection of those two is the point where the item is located
     *
     *    For this, as an example, as as we will proceed:
     *    A - B = (A - D)x + (B - E)y + (C - F) = 0
     *    B - C = (D - G)x + (E - H)y + (F - I) = 0
     *
     *    A - B => x = ((E - B)y + (F - C) )/(A - D)
     *    B - C => x = ((H - E)y + (I - F) )/(D - G)
     *
     *    A - B = B - C
     *
     *    y = ((A - D)*(I - F) - (D - G)*(F - C)) / ((D - G)*(E - B) - (A - D)*(H - E))
     *
     *    This method receives the vectors containing the values that the user has input before, it returns the
     *    coordinates where the item is located.
     * */
        double A = 2*x[0];
        double B = 2*y[0];
        double C = Math.pow(x[0],2.0)+Math.pow(y[0],2.0)-Math.pow(r[0],2.0);
        double D = 2*x[1];
        double E = 2*y[1];
        double F = Math.pow(x[1],2.0)+Math.pow(y[1],2.0)-Math.pow(r[1],2.0);
        double G = 2*x[2];
        double H = 2*y[2];
        double I = Math.pow(x[2],2.0)+Math.pow(y[2],2.0)-Math.pow(r[2],2.0);

        if(D == G && D != A){
            Double auxA = A;
            Double auxB = B;
            Double auxC = C;
            A = D;
            B = E;
            C = F;
            D = auxA;
            E = auxB;
            F = auxC;
        }

        //The value of the coordinate Y where the item is located
        double coordinateY = -((A - D)*(I - F) - (D - G)*(F - C)) / ((D - G)*(E - B) - (A - D)*(H - E));
        //The value of the coordinate X where the item is located
        double coordinateX = ((H - E)*coordinateY + (I - F))/(D - G);


        LatLng latLang = new LatLng(-coordinateX, coordinateY);
        Log.d("sss", "Final Latitude "+coordinateX+ " and Longitude "+coordinateY);

        return latLang;
    }
}
