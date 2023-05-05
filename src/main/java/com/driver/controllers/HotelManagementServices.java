package com.driver.controllers;

import com.driver.model.Booking;
import com.driver.model.Facility;
import com.driver.model.Hotel;
import com.driver.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class HotelManagementServices {

    HotelManagementRepository hotelManagementRepository=new HotelManagementRepository();



    public boolean addHotel(Hotel hotel) {

       String name=hotel.getHotelName();
       if(name==null)
           return false;
        Optional<Hotel> hotel1=hotelManagementRepository.getHotelByName(hotel.getHotelName());
        if(hotel1.isPresent())
            return false;
        hotelManagementRepository.addHotel(hotel);
        return true;

    }
    public  Integer addUser(User user) {
        //You need to add a User Object to the database
        //Assume that user will always be a valid user and return the aadharCardNo of the user
        Optional<User> userOptional=hotelManagementRepository.addUser(user);

        return userOptional.get().getaadharCardNo();
    }

    public String getHotelWithMostFacilities() {
        //Out of all the hotels we have added so far, we need to find the hotelName with most no of facilities
        //Incase there is a tie return the lexicographically smaller hotelName
        //Incase there is not even a single hotel with atleast 1 facility return "" (empty string)
        Optional<String> hotelName=hotelManagementRepository.getHotelWithMostFacilities();
        if(hotelName.isEmpty())
            return "";
        else
            return hotelName.get();
    }

    public  Hotel updateFacilities(List<Facility> newFacilities, String hotelName) {

        //We are having a new facilites that a hotel is planning to bring.
        //If the hotel is already having that facility ignore that facility otherwise add that facility in the hotelDb//done
        //return the final updated List of facilities and also update that in your hotelDb//done
        //Note that newFacilities can also have duplicate facilities possible//done
        Optional<Hotel> hotel=hotelManagementRepository.getHotelByName(hotelName);
        if(hotel.isEmpty())
            return hotel.get();
        List<Facility> oldFacilities=hotelManagementRepository.getFacilitiesOfHotel(hotelName);
        for(Facility facility:newFacilities)
        {
            if(Facility.valueOf(facility.name())!=null) {
                if(!oldFacilities.contains(facility))
                    oldFacilities.add(facility);
            }
        }

        return hotelManagementRepository.updateFacilities(oldFacilities,hotelName);
    }

    public String generateBookingId()
    {
        String bookingId= UUID.randomUUID().toString();
        while(!hotelManagementRepository.uniqueId(bookingId))
        {
            bookingId= UUID.randomUUID().toString();
        }
        return bookingId;
    }
    public int amountToBePaid(Booking booking)
    {
        int numberOfRooms=booking.getNoOfRooms();
        Optional<Hotel> hotel=hotelManagementRepository.getHotelByName(booking.getHotelName());
        int pricePerNight=hotel.get().getPricePerNight();

        return (pricePerNight*numberOfRooms);
    }

    public boolean doesUserExist(int bookingAadharCard) {
        Optional<User> user=hotelManagementRepository.getUserByAadhar(bookingAadharCard);
        if(user.isEmpty())
            return false;
        else
            return true;
    }

    public  boolean doesHotelExist(String hotelName) {
        Optional<Hotel> hotel=hotelManagementRepository.getHotelByName(hotelName);
        if(hotel.isEmpty())
            return false;
        else
            return true;
    }

    public boolean isRoomsAvailable(int noOfRooms, String hotelName) {
        Hotel hotel=hotelManagementRepository.getHotelByName(hotelName).get();
        if(noOfRooms>hotel.getAvailableRooms())
            return false;
        else
            return true;

    }

    public int bookARoom(Booking booking) {
        String hotelName=booking.getHotelName();
        String personName=booking.getBookingPersonName();
        if(hotelName==null||personName==null)
            return -1;
        boolean checkHotel=doesHotelExist(hotelName);
        boolean checkUser=doesUserExist(booking.getBookingAadharCard());
        boolean checkRooms=isRoomsAvailable(booking.getNoOfRooms(),booking.getHotelName());
        if(checkHotel==false||checkUser==false||checkRooms==false)
           return 1;

        Hotel hotel=hotelManagementRepository.getHotelByName(booking.getHotelName()).get();

        String bookingId=generateBookingId();
        booking.setBookingId(bookingId);
        int amountToBePaid= amountToBePaid(booking);
        booking.setAmountToBePaid(amountToBePaid);

        hotelManagementRepository.bookARoom(booking);
       // Hotel hotel=hotelManagementRepository.getHotelByName(booking.getHotelName()).get();
        hotel.setAvailableRooms(hotel.getAvailableRooms()-booking.getNoOfRooms());
        return amountToBePaid;
    }


    public Optional<Hotel> getHotelByName(String hotelName) {
        return hotelManagementRepository.getHotelByName(hotelName);
    }

    public int getBookingDoneByUser(Integer aadharCard) {
        Optional<User> user=hotelManagementRepository.getUserByAadhar(aadharCard);
        if(user.isEmpty())
            return 0;
        List<String> getAllbookings=hotelManagementRepository.getBookingDoneByUser(aadharCard);
        return getAllbookings.size();

    }
}
