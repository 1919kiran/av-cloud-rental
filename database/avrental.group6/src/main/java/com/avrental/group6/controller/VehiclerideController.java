package com.avrental.group6.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.avrental.group6.service.VehiclerideServices;
import com.avrental.group6.dao.VehiclerideRepository;

import com.avrental.group6.model.User;
import com.avrental.group6.model.Vehicle;
import com.avrental.group6.model.Reservation;

@Repository
@RestController
public class VehiclerideController {
	private VehiclerideServices vehiclerideServices;
	@Autowired
	EntityManager em;

	@Autowired
	VehiclerideRepository vehiclerideRepository;

	@Autowired
	public void setVehicleRideService(VehiclerideServices vehiclerideService) {
		this.vehiclerideServices = vehiclerideService;
	}

	/*
	 * Shows all vehicles
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/vehicleride/list")
	public List<Reservation> listVehiclesride(Model model){

		ArrayList<Reservation> listOfVehicleride = new ArrayList<Reservation>();

		model.addAttribute("vehicleride", vehiclerideServices.listAll());

		Map<String, Object> modelMap = model.asMap();
		listOfVehicleride  = (ArrayList<Reservation>) modelMap.get("vehicleride");
		
		
		ArrayList<Long> vehicleDateTime=new ArrayList<Long>();
		for (int i = 0; i < listOfVehicleride.size(); i++) 
		{
			vehicleDateTime.add(Long.parseLong(listOfVehicleride.get(i).getStart_time().replace("-", "").replace(" ", "").replace(":", "")));
		}
		
		//Collections.sort(vehicleDateTime);
		Collections.sort(vehicleDateTime, Collections.reverseOrder());
		
		
		for (int j = 0; j < vehicleDateTime.size(); j++) 
		{
            System.out.print(vehicleDateTime.get(j)+ " ");  
		}
		
		
		ArrayList<Reservation> listOfVehiclerideReturn = new ArrayList<Reservation>();
		for (int k = 0; k < vehicleDateTime.size(); k++) 
		{
			
			for (int l = 0; l < listOfVehicleride.size(); l++) 
			{
	           if(listOfVehicleride.get(l).getStart_time().replace("-", "").replace(" ", "").replace(":", "").equalsIgnoreCase(vehicleDateTime.get(k).toString()))
	           {
	        	   listOfVehiclerideReturn.add(listOfVehicleride.get(l));
	           }
			}
			
			
			
		}

		return listOfVehiclerideReturn;
	}

	/*
	 * Shows property by vehicleID
	 */
	@RequestMapping("vehicleride/show/{id}")
	public Reservation getVehicleride(@PathVariable String id, Model model){
		model.addAttribute("vehicleride", vehiclerideServices.getById(String.valueOf(id)));
		ArrayList<Reservation> listOfVehicleride = new ArrayList<Reservation>();        
		Map<String, Object> modelMap = model.asMap();
		listOfVehicleride=(ArrayList<Reservation>) modelMap.get("vehicleride");
		return listOfVehicleride.get(0);
	}


	/*
	 * Add new vehicles to the table
	 */
	@RequestMapping(method = RequestMethod.POST, value = "vehicleride/add")
	public void addVehicleride(@RequestBody Reservation vehicleride)
	{
		vehiclerideServices.saveVehicleride(vehicleride);
	}



	/*
	 * Delete vehicleride by ID
	 */
	@RequestMapping("/vehicleride/delete/{id}")
	public String delete(@PathVariable String id){
		vehiclerideServices.delete(id);
		return "delete successful";
	}
	
	/*
	 * Search vehicle by any vehicle parameter
	 */
	@RequestMapping(method = RequestMethod.GET, value = "vehicleride/search")
	public List<Reservation> getVehicleride(@RequestParam Map<String, String> customQuery)
	{
		return vehiclerideServices.constructQuery(customQuery).getResultList();

	}
	

}
