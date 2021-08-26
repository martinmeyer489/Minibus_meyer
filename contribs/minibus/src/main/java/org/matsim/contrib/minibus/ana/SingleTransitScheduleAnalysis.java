package org.matsim.contrib.minibus.ana;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.pt.transitSchedule.api.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class SingleTransitScheduleAnalysis {
    public static void main(String[] args) throws IOException {

        //



        String path= "C:/Users/marti/Documents/MA/output_1pct_no_sub_cap_3_earning_100/";


        File parameters = new File(path+"parameters.csv");
        FileWriter fw_parameters = new FileWriter(parameters);
        BufferedWriter bw_parameters = new BufferedWriter(fw_parameters);
        bw_parameters.write("seed,network_length,stops,Vehicle_hours,VehicleKilometer");
        bw_parameters.newLine();

        //for loop over different seeds


        //read transitschedule and network
        Scenario scenario = ScenarioUtils.createScenario(ConfigUtils.createConfig());
        new MatsimNetworkReader(scenario.getNetwork()).readFile("C:/Users/marti/Documents/MA/input/current standard input/berlin-v5.5.3-1pct.output_network.xml.gz");
        new TransitScheduleReader(scenario).readFile("C:/Users/marti/Documents/MA/output_1pct_no_sub_cap_3_earning_100/ITERS/it.12/basecase.12.transitScheduleScored.xml.gz");
        TransitSchedule transitSchedule = scenario.getTransitSchedule();




        // get links of transit routes

        File links = new File(path+"links.csv");
        FileWriter fw = new FileWriter(links);
        BufferedWriter bw_links = new BufferedWriter(fw);


        bw_links.write("Id,Transit_Line,Transit_Route");
        bw_links.newLine();


        for (TransitLine transitLine: transitSchedule.getTransitLines().values()){
            for (TransitRoute transitRoute: transitLine.getRoutes().values()) {
                if (transitLine.getId().toString().contains("para")) {
                    for (Id id : transitRoute.getRoute().getLinkIds()) {
                        bw_links.write(String.valueOf(id) + "," + String.valueOf(transitLine.getId()+ "," + String.valueOf(transitRoute.getId())));
                        bw_links.newLine();
                    }
                }

            }
        }

        //get stops of para transit lines

        File stops = new File(path+"stops.csv");
        FileWriter fw_stops = new FileWriter(stops);
        BufferedWriter bw_stops = new BufferedWriter(fw_stops);


        bw_stops.write("Stop_ID,X,Y");
        bw_stops.newLine();


        for (TransitLine transitLine: transitSchedule.getTransitLines().values()){
            for( TransitRoute transitRoute:transitLine.getRoutes().values()) {
                for (TransitRouteStop transitRouteStop : transitRoute.getStops()) {
                    if (transitRouteStop.getStopFacility().getId().toString().contains("para")) {
                        bw_stops.write(String.valueOf(transitRouteStop.getStopFacility().getId()) + "," + String.valueOf(transitRouteStop.getStopFacility().getCoord().getX()) + "," + String.valueOf(transitRouteStop.getStopFacility().getCoord().getY()));
                        bw_stops.newLine();
                    }
                }
            }
        }






        // calculate network length, number of stops, Vehicle Hours,



        Set<Id<Link>> linkIds = new HashSet<>();
        Set<String> stopIds = new HashSet<>();


        double totVehKM = 0.0;
        double totVehH = 0.0;
        for (TransitLine line: transitSchedule.getTransitLines().values()) {
            for (TransitRoute route : line.getRoutes().values()) {
                if (line.getId().toString().contains("para")) {
                    double vehKM = 0.0;
                    for (Id<Link> link : route.getRoute().getLinkIds()) {
                        vehKM += scenario.getNetwork().getLinks().get(link).getLength() / 1000;
                    }
                    double vehH = 0.0;
                    vehH = (route.getStops().get(route.getStops().size() - 1).getArrivalOffset().seconds() -
                            route.getStops().get(0).getDepartureOffset().seconds()) / 3600;

                    totVehKM += (route.getDepartures().size() * vehKM);
                    totVehH += (route.getDepartures().size() * vehH);

                }
            }
        }

        for (TransitLine line: transitSchedule.getTransitLines().values()) {
            for (TransitRoute route : line.getRoutes().values()) {
                if (line.getId().toString().contains("para")) {

                    //if(route.getTransportMode().contains("bus")){
                    for (Id<Link> link : route.getRoute().getLinkIds()) {
                        linkIds.add(link);
                    }
                    for (TransitRouteStop stop: route.getStops()) {
                        stopIds.add(stop.getStopFacility().getId().toString());
                    }
                }
            }
        }


        double networkLength = 0.0;
        for(Id<Link> link: linkIds) {
            networkLength += scenario.getNetwork().getLinks().get(link).getLength();
        }

        bw_parameters.write(String.valueOf(1)+","+String.valueOf(networkLength/ 1000.0)+","+String.valueOf(stopIds.size())+","+String.valueOf(totVehH)+","+String.valueOf(totVehKM));
        bw_parameters.newLine();


        System.out.println(" network lenght in km "+networkLength / 1000.0);
        System.out.println(" number of stops "+stopIds.size());

        System.out.println(" total Vehiclekilometer  "+totVehKM);
        System.out.println(" total Vehiclehours  "+totVehH);




        // frequency departures
        File depart = new File(path+"departures.csv");
        FileWriter fw_depart= new FileWriter(depart);
        BufferedWriter bw_depart = new BufferedWriter(fw_depart);


        bw_depart.write("transitline,vehicle,departuretime");
        bw_depart.newLine();



        for (TransitLine transitLine: transitSchedule.getTransitLines().values()){
            for (TransitRoute transitRoute : transitLine.getRoutes().values()) {
                if (transitLine.getId().toString().contains("para")) {

                    //if(transitRoute.getTransportMode().contains("bus")){

                    for (Departure departure : transitRoute.getDepartures().values()) {
                        bw_depart.write(String.valueOf(transitLine.getId()) +","+String.valueOf(departure.getVehicleId())+ "," + String.valueOf((int)(departure.getDepartureTime()/3600)));
                        bw_depart.newLine();
                    }
                }
            }
        }
        bw_depart.close();

        // frequency vehicles in service
        File freq = new File(path+"frequency.csv");
        FileWriter fw_freq= new FileWriter(freq);
        BufferedWriter bw_freq = new BufferedWriter(fw_freq);


        bw_freq.write("transitRoute,departuretime,arrivaltime");
        bw_freq.newLine();






        for (TransitLine transitLine: transitSchedule.getTransitLines().values()){
            for (TransitRoute transitRoute : transitLine.getRoutes().values()) {
                if (transitLine.getId().toString().contains("para")) {

                    //if(transitRoute.getTransportMode().contains("bus")){
                    //get duration of transit route by departureoffset of the last stop on the route
                    double duration=transitRoute.getStops().get(transitRoute.getStops().size()-1).getDepartureOffset().seconds();
                    for (Departure departure : transitRoute.getDepartures().values()) {
                        bw_freq.write(String.valueOf(transitRoute.getId()) + "," + String.valueOf((int)(departure.getDepartureTime()/3600))+ "," + String.valueOf((int)(((departure.getDepartureTime()+duration)/3600))));
                        bw_freq.newLine();

                    }
                }
            }

        }
        bw_freq.close();














        bw_parameters.close();






    }



}