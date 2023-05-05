package com.driver.controllers;

import com.driver.model.Booking;
import com.driver.model.Facility;
import com.driver.model.Hotel;
import com.driver.model.User;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class HotelManagementRepository {
    private Map<String, Hotel> hotelMap=new HashMap<>();//hotel name-Hotel database
    private Map<Integer, User> userMap=new HashMap<>();//userAadhar-User database

    private Map<String,List<String>> hotelBookingMap=new HashMap<>();//hotelName-booingId database

    private Map<Integer,List<String>> userBookingMap=new HashMap<>();//userAadhar-bookingIds database

    private Map<String,List<Facility>> hotelFacilityMap=new TreeMap<>();//hotel-facilties map

    private Map<String, Booking> bookingMap=new HashMap<>();//all bookings database



    public  Optional<Hotel> getHotelByName(String hotelName) {
        if(hotelName==null)
            return Optional.empty();
        if(hotelMap.containsKey(hotelName))
            return Optional.of(hotelMap.get(hotelName));
        else
            return Optional.empty();

    }

    public  void addHotel(Hotel hotel) {
        hotelMap.put(hotel.getHotelName(),hotel);//adding to hotelDatabse

        List<String> hotelUserBookings=new ArrayList<>();
        hotelBookingMap.put(hotel.getHotelName(), hotelUserBookings);//opened booking for hotel

        List<Facility> facilities=hotel.getFacilities();
        if(facilities.equals(null))
            facilities=new ArrayList<>();
        hotelFacilityMap.put(hotel.getHotelName(),facilities);//hotel-facilities database
    }

    public Optional<User> addUser(User user) {
        if(!userMap.containsKey(user.getaadharCardNo()))
        {
            userMap.put(user.getaadharCardNo(),user);
            List<String> list=new ArrayList<>();//new booking ids list
            userBookingMap.put(user.getaadharCardNo(),list);
        }
        return Optional.of(user);
    }

    public Optional<String> getHotelWithMostFacilities() {
        String hotelName="";
        int facilityCount=-1;
        if(hotelMap.isEmpty())
            return Optional.empty();
        for(String name:hotelFacilityMap.keySet())
        {
            int size=hotelFacilityMap.get(name).size();
            if(size>facilityCount)
            {
                hotelName=name;
                facilityCount=size;

            }
        }
        if(hotelName.equals("")||facilityCount==0)
            return Optional.empty();
        else
            return Optional.of(hotelName);
    }

    public  List<Facility> getFacilitiesOfHotel(String hotelName) {
       return hotelMap.get(hotelName).getFacilities();
    }

    public Hotel updateFacilities(List<Facility> oldFacilities, String hotelName) {
        Hotel hotel=hotelMap.get(hotelName);
        hotel.setFacilities(oldFacilities);
        hotelFacilityMap.put(hotelName,oldFacilities);
        return hotel;
    }

    public boolean uniqueId(String bookingId) {
        if(bookingMap.containsKey(bookingId))
            return false;
        else return true;
    }

    public Optional<User> getUserByAadhar(int bookingAadharCard) {
        if(userMap.containsKey(bookingAadharCard))
            return Optional.of(userMap.get(bookingAadharCard));
        else
            return Optional.empty();
    }


    public int bookARoom(Booking booking) {


        if(!bookingMap.containsKey(booking.getBookingId())) {

            Hotel hotel = hotelMap.get(booking.getHotelName());
            hotel.setAvailableRooms(hotel.getAvailableRooms() - booking.getNoOfRooms());
            hotelMap.put(booking.getHotelName(), hotel);

            bookingMap.put(booking.getBookingId(), booking);//adding to bookings map

            List<String> bookingList = hotelBookingMap.get(booking.getHotelName());
            bookingList.add(booking.getBookingId());
            hotelBookingMap.put(booking.getHotelName(), bookingList);//added to hotel-bookings

            List<String> allBookings = userBookingMap.get(booking.getBookingAadharCard());
            allBookings.add(booking.getBookingId());
            userBookingMap.put(booking.getBookingAadharCard(), allBookings);
            return booking.getAmountToBePaid();
        }
        else
            return -1;




    }

    public List<String> getBookingDoneByUser(Integer aadharCard) {
        return userBookingMap.get(aadharCard);
    }
}
