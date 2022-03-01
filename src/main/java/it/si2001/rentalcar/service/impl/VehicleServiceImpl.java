package it.si2001.rentalcar.service.impl;

import it.si2001.rentalcar.entity.Vehicle;
import it.si2001.rentalcar.repository.VehicleRepository;
import it.si2001.rentalcar.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class VehicleServiceImpl implements VehicleService {

    @Autowired
    VehicleRepository vehicleRepository;





    @Override
    public Vehicle insertVehicle(Vehicle v) {

        try{

            Optional<Vehicle> vehicle = vehicleRepository.findByLicensePlate(v.getLicensePlate());

            if(vehicle.isPresent()){

                return null;

            }

            else if(v.getIdVehicle() != null){

                Optional<Vehicle> vehicleWithId = vehicleRepository.findByIdVehicle(v.getIdVehicle());

                if(vehicleWithId.isPresent()){

                    return null;

                }

            }

            vehicleRepository.saveAndFlush(v);

            return v;

        }
        catch (Exception e){

            e.printStackTrace();

            throw e;

        }

    }





    @Override
    public boolean deleteVehicle(Long id) {

        try{

            Optional<Vehicle> vehicle = vehicleRepository.findByIdVehicle(id);

            if(vehicle.isPresent()){

                vehicleRepository.delete(vehicle.get());

                return true;

            }

            return false;

        }
        catch (Exception e){

            e.printStackTrace();

            throw e;

        }

    }





    @Override
    public Vehicle updateVehicle(Vehicle v, Long id) {

        try{

           Optional<Vehicle> vehicleToUpdate = vehicleRepository.findByIdVehicle(id);

           if(vehicleToUpdate.isPresent()){

               vehicleToUpdate.get().setLicensePlate(v.getLicensePlate());
               vehicleToUpdate.get().setManufacturer(v.getManufacturer());
               vehicleToUpdate.get().setModel(v.getModel());
               vehicleToUpdate.get().setRegistrYear(v.getRegistrYear());
               vehicleToUpdate.get().setTypology(v.getTypology());

               vehicleRepository.saveAndFlush(vehicleToUpdate.get());

               return vehicleToUpdate.get();

           }

           return null;

        }
        catch (Exception e){

            e.printStackTrace();

            throw e;

        }

    }





    @Override
    public Vehicle fetchVehicle(Long id) {

        try{

            Optional<Vehicle> v = vehicleRepository.findByIdVehicle(id);

            return v.orElse(null);

        }
        catch (Exception e){

            e.printStackTrace();

            throw e;

        }

    }





    @Override
    public List<Vehicle> fetchVehicles() {

        try{

            List<Vehicle> vehicles = vehicleRepository.findAll();

            if(vehicles.isEmpty()){

                return null;

            }

            return vehicles;

        }
        catch (Exception e){

            e.printStackTrace();

            throw e;

        }

    }

}
