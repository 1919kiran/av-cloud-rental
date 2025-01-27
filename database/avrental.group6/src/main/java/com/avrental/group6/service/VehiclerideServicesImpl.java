package com.avrental.group6.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.avrental.group6.dao.VehicleRepository;
import com.avrental.group6.dao.VehiclerideRepository;

import com.avrental.group6.model.User;
import com.avrental.group6.model.Vehicle;
import com.avrental.group6.model.Reservation;
import com.avrental.group6.model.VehicleStatus;

@Service
public class VehiclerideServicesImpl implements VehiclerideServices{

    private VehiclerideRepository vehiclerideRepository;

    @Autowired
    EntityManager em;
    
    @Autowired
    public VehiclerideServicesImpl(VehiclerideRepository vehiclerideRepository) {
        this.vehiclerideRepository = vehiclerideRepository;
    }
    /*
     * Service method to list all vehiclesride
     */
    @Override
    public List<Reservation> listAll() {
        List<Reservation> vehiclesride = new ArrayList<>();
        vehiclerideRepository.findAll().forEach(vehiclesride::add);
        return vehiclesride;
    }

    /*
     * Service method to list vehicleride by id
     */
    @Override
    public Iterable<Reservation> getById(String id) {
    	
        List <String> ids = new ArrayList<String>();
        ((ArrayList<String>) ids).add(id);
		return vehiclerideRepository.findAllById(ids);
    }

   
    /*
     * Service method to save new vehiclesride. This method is to be used for new vehicleride registration.
     */
	@Override
	public void saveVehicleride(Reservation vehicleride) {
		// TODO Auto-generated method stub
		try {
			Reservation savedVehicle=vehiclerideRepository.save(vehicleride);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	/*
	 * Service method to delete vehiclesride, when Administrator wants to delete any vehicleride.
	 */
    @Override
    public void delete(String id) {
        vehiclerideRepository.deleteById(id);

    }
    
    
    /*
	 * Service method to search vehicleride using any vehicleride parameters.
	 */
	@Override
	public TypedQuery<Reservation> constructQuery(Map<String, String> customQuery) {
		CriteriaBuilder cb = null;
		try {
			cb = em.getCriteriaBuilder();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		CriteriaQuery<Reservation> cq = cb.createQuery(Reservation.class);
		Root<Reservation> vehicleride = cq.from(Reservation.class);
		Predicate existingpredicate = null;
		int predicateCount=0;
		Predicate masterPredicate=null;

		try {

			for (Map.Entry<String,String> entry : customQuery.entrySet())  
			{
				if(vehicleride.get(entry.getKey().toString()) != null)
				{
					
					//Query for range values with comma(,) as delimiter
					if(entry.getValue().contains(","))
					{
						int minRange=Integer.parseInt(customQuery.get(entry.getKey().toString()).split(",")[0]);
						int maxRange=Integer.parseInt(customQuery.get(entry.getKey().toString()).split(",")[1]);
						if(predicateCount==0)
						{
							masterPredicate = cb.between(vehicleride.get(entry.getKey().toString()),minRange, maxRange );
						}
						else
						{
							existingpredicate = cb.between(vehicleride.get(entry.getKey().toString()),minRange, maxRange );
							masterPredicate=cb.and(masterPredicate,existingpredicate);
						}
						predicateCount++;
					}
					//Query for equals values
					else
					{
						
						if(predicateCount==0)
						{
							masterPredicate = cb.equal(vehicleride.get(entry.getKey().toString()), customQuery.get(entry.getKey().toString()));
						}
						else
						{
							existingpredicate = cb.equal(vehicleride.get(entry.getKey().toString()), customQuery.get(entry.getKey().toString()));
							masterPredicate=cb.and(masterPredicate,existingpredicate);
						}
						predicateCount++;
						//cq.where(predicate);
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		cq.where(masterPredicate);
		TypedQuery<Reservation> query = em.createQuery(cq);
		return query;
	}
	

}
