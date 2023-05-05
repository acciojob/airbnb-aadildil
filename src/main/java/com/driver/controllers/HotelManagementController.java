package com.driver.controllers;

import com.driver.model.Booking;
import com.driver.model.Facility;
import com.driver.model.Hotel;
import com.driver.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/hotel")
public class HotelManagementController {


    HotelManagementServices hotelManagementServices=new HotelManagementServices();
    @PostMapping("/add-hotel")
    public String addHotel(@RequestBody Hotel hotel){


        if(hotel.getHotelName().equals("")||hotel.getHotelName().equals(null))
            return "FAILURE";
        boolean added=hotelManagementServices.addHotel(hotel);


        if(added)
            return "SUCCESS";
        else return "FAILURE";


    }

    @PostMapping("/add-user")
    public Integer addUser(@RequestBody User user){

        //You need to add a User Object to the database
        //Assume that user will always be a valid user and return the aadharCardNo of the user



       return hotelManagementServices.addUser(user);
    }

    @GetMapping("/get-hotel-with-most-facilities")
    public String getHotelWithMostFacilities(){

        //Out of all the hotels we have added so far, we need to find the hotelName with most no of facilities
        //Incase there is a tie return the lexicographically smaller hotelName
        //Incase there is not even a single hotel with atleast 1 facility return "" (empty string)

        return hotelManagementServices.getHotelWithMostFacilities();
    }

    @PostMapping("/book-a-room")
    public int bookARoom(@RequestBody Booking booking){




        return hotelManagementServices.bookARoom(booking);
        //The booking object coming from postman will have all the attributes except bookingId and amountToBePaid;//done
        //Have bookingId as a random UUID generated String//done
        //save the booking Entity and keep the bookingId as a primary key
        //Calculate the total amount paid by the person based on no. of rooms booked and price of the room per night.
        //If there arent enough rooms available in the hotel that we are trying to book return -1 
        //in other case return total amount paid 
        

    }
    
    @GetMapping("/get-bookings-by-a-person/{aadharCard}")
    public int getBookings(@PathVariable("aadharCard")Integer aadharCard)
    {
        //In this function return the bookings done by a person 
        return hotelManagementServices.getBookingDoneByUser(aadharCard);
    }

    @PutMapping("/update-facilities")
    public Hotel updateFacilities(@RequestParam List<Facility> newFacilities, @RequestParam String hotelName){

        //We are having a new facilites that a hotel is planning to bring.
        //If the hotel is already having that facility ignore that facility otherwise add that facility in the hotelDb
        //return the final updated List of facilities and also update that in your hotelDb
        //Note that newFacilities can also have duplicate facilities possible
        return hotelManagementServices.updateFacilities(newFacilities,hotelName);
    }

    @GetMapping("/get-hotel-by-name")
    public Hotel getHotelByName(@RequestParam String hotelName) throws Exception {
       Optional<Hotel> hotel= hotelManagementServices.getHotelByName(hotelName);
       if(hotel.isEmpty())
           throw new Exception( "hotel doesnt exist");
       else
           return hotel.get();
    }
}
