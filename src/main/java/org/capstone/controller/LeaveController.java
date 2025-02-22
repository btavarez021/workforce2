package org.capstone.controller;

import org.capstone.entity.Leave;
import org.capstone.entity.LeaveResponse;
import org.capstone.exception.LeaveException;
import org.capstone.exception.LeaveFinancialException;
import org.capstone.exception.LeaveManagerNotFoundException;
import org.capstone.exception.LeaveNotFoundException;
import org.capstone.repository.LeaveRepository;
import org.capstone.service.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
public class LeaveController {

    private final LeaveService leaveService;

    @Autowired
    public LeaveController(LeaveService leaveService) {
        this.leaveService = leaveService;
    }

    /**
     * Endpoints for localhost:9000/leave
     */

    // Endpoint for getting all leaves
    @GetMapping(value = "/leave")
    public ResponseEntity<?> getLeave() throws LeaveException {
        try {
        List<Leave> l = leaveService.getAllLeaves();
        return new ResponseEntity<>(l, HttpStatus.OK);
    } catch (LeaveException e) {
        System.err.println("No leaves submitted" + e.getMessage());
        String errorResponse = "No leaves submitted: " + e.getMessage();
        return new ResponseEntity<>(errorResponse, HttpStatus.OK);

    }
    }

//    Endpoint for getting all leaves for all employees by a manager
    @GetMapping(value = "/manager/{managerID}/leave")
    public ResponseEntity<Object> getAllEmployeeLeaveByManager(@PathVariable int managerID) throws LeaveManagerNotFoundException {
        try {
            List<Leave> l = leaveService.getAllEmployeeLeavesForManager(managerID);
            return new ResponseEntity<>(l, HttpStatus.OK);
        }
        catch (LeaveManagerNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }

    }

    //    Endpoint for getting pending leaves for all employees assigned to a manager
    @GetMapping(value = "/manager/{managerID}/leave/pending")
    public ResponseEntity<Object> getPendingEmployeeLeaveByManager(@PathVariable int managerID) throws LeaveManagerNotFoundException {
        try {
            boolean activeFlag = true;
            boolean acceptedFlag = false;
            List<Leave> l = leaveService.getEmployeeLeavesForManagerByStatus(managerID, activeFlag, acceptedFlag);
            return new ResponseEntity<>(l, HttpStatus.OK);
        }
        catch (LeaveManagerNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }

    }

    //    Endpoint for getting approved leaves for all employees assigned to a manager
    @GetMapping(value = "/manager/{managerID}/leave/approved")
    public ResponseEntity<Object> getApprovedEmployeeLeaveByManager(@PathVariable int managerID) throws LeaveManagerNotFoundException {
        try {
            boolean activeFlag = false;
            boolean acceptedFlag = true;
            List<Leave> l = leaveService.getEmployeeLeavesForManagerByStatus(managerID, activeFlag, acceptedFlag);
            return new ResponseEntity<>(l, HttpStatus.OK);
        }
        catch (LeaveManagerNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }

    }

    //    Endpoint for getting rejected leaves for all employees assigned to a manager
    @GetMapping(value = "/manager/{managerID}/leave/rejected")
    public ResponseEntity<Object> getRejectedEmployeeLeaveByManager(@PathVariable int managerID) throws LeaveManagerNotFoundException {
        try {
            boolean activeFlag = false;
            boolean acceptedFlag = false;
            List<Leave> l = leaveService.getEmployeeLeavesForManagerByStatus(managerID, activeFlag, acceptedFlag);
            return new ResponseEntity<>(l, HttpStatus.OK);
        }
        catch (LeaveManagerNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }

    }

    // Endpoint for getting all leaves by employee
    @GetMapping(value = "/employee/{employeeID}/leave")
    public ResponseEntity<List<Leave>> getLeaveByEmployeeID(@PathVariable int employeeID) throws LeaveException {
        try{
            List<Leave> leaves = leaveService.getAllLeaveByEmployeeId(employeeID);
            return ResponseEntity.ok(leaves);
        }catch (LeaveException e){
            return ResponseEntity.ok(new ArrayList<Leave>());
        }

    }

    // Endpoint for getting all leaves by employee & accept status
    @GetMapping(value = "/employee/{employeeID}/leave/accepted")
    public ResponseEntity<?> getLeaveByEmployeeIdAndAccepted(@PathVariable int employeeID) throws LeaveException {
        try {
            List<Leave> leaves = leaveService.getAllLeaveByEmployeeIdAndAcceptFlag(employeeID, true);
            return ResponseEntity.ok(leaves);
        } catch (LeaveException e) {
            System.err.println("Error getting approved leaves: " + e.getMessage());
            String errorResponse = "Error getting approved leaves: " + e.getMessage();
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    // Endpoint for getting all leaves by employee & rejected leaves
    @GetMapping(value = "/employee/{employeeID}/leave/rejected")
    public ResponseEntity<?> getLeaveByEmployeeIdAndRejected(@PathVariable int employeeID) throws LeaveException {
        try {
            List<Leave> leaves = leaveService.getAllLeaveByEmployeeIdAndAcceptFlag(employeeID, false); // Explicitly set acceptFlag to false
            return ResponseEntity.ok(leaves);
        } catch (LeaveException e) {
            System.err.println("Error getting rejected leaves: " + e.getMessage());
            String errorResponse = "Error getting rejected leaves: " + e.getMessage();
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);

        }
    }

    // Endpoint for getting all leaves by employee & active leaves
    @GetMapping(value = "/employee/{employeeID}/leave/active")
    public ResponseEntity<?> getLeaveByEmployeeIdAndActive(@PathVariable int employeeID) throws LeaveException {
        try {
            List<Leave> leaves = leaveService.getAllLeavesByEmployeeIdAndActiveFlag(employeeID, true); // Default active to true
            return ResponseEntity.ok(leaves);
        } catch (LeaveException e) {
            System.err.println("Error getting pending leaves: " + e.getMessage());
            String errorResponse = "Error getting pending leaves for an employee: " + e.getMessage();
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    // Endpoint for inactive leaves
    @GetMapping(value = "/employee/{employeeID}/leave/inactive")
    public ResponseEntity<?> getLeaveByEmployeeIdAndInactive(@PathVariable int employeeID) throws LeaveException {
        try {
            List<Leave> leaves = leaveService.getAllLeavesByEmployeeIdAndActiveFlag(employeeID, false); // Explicitly set active to false
            return ResponseEntity.ok(leaves);
        } catch (LeaveException e) {
            System.err.println("Error getting non-pending leaves: " + e.getMessage());
            String errorResponse = "Error getting non-pending leaves for an employee: " + e.getMessage();
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }


    @PutMapping("/employee/{employeeId}/leave/{id}")
    public ResponseEntity<Leave> updateLeave(@RequestBody Leave l, @PathVariable int employeeId, @PathVariable int id) {
        try {
            Leave updatedLeave = leaveService.updateLeave(id, l);
            return new ResponseEntity<>(updatedLeave, HttpStatus.OK);
        } catch (LeaveNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    /**Create a custom response object that encapsulates the status code, status message,
     * and the updated leave object (if applicable) to be sent to front end */
    @PutMapping("/leave/{id}")
    public ResponseEntity<LeaveResponse> updateLeaveById(@RequestBody Leave l, @PathVariable int id) {
        try {
            Leave updatedLeave = leaveService.updateLeaveById(id, l);
            LeaveResponse response = new LeaveResponse(HttpStatus.OK, "Leave updated successfully", updatedLeave);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (LeaveNotFoundException e) {
            LeaveResponse response = new LeaveResponse(HttpStatus.BAD_REQUEST, "Leave is not updated: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (LeaveFinancialException e) {
            LeaveResponse response = new LeaveResponse(HttpStatus.FAILED_DEPENDENCY, "Leave is not updated: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.FAILED_DEPENDENCY);
        }
    }

    @GetMapping("/leave/{id}/accept")
    public ResponseEntity<Leave> acceptLeave(@PathVariable int id) {
        try {
            Leave updatedLeave = leaveService.updateAcceptedFlag(id, true);
            return new ResponseEntity<>(updatedLeave, HttpStatus.OK);
        } catch (LeaveException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/leave/{id}/reject")
    public ResponseEntity<Leave> rejectLeave(@PathVariable int id) {
        try {
            Leave updatedLeave = leaveService.updateAcceptedFlag(id, false);
            return new ResponseEntity<>(updatedLeave, HttpStatus.OK);
        } catch (LeaveException e) {
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping("leave/{leaveId}")
    public ResponseEntity<?> deleteLeave(@PathVariable int leaveId) {
        try {
            Leave leave = leaveService.deleteLeave(leaveId);
            return new ResponseEntity<>("Leave successfully deleted", HttpStatus.OK);
        } catch (LeaveNotFoundException e) {
            // Even if product is not found, return 200 status - this is convention
            return new ResponseEntity<>(e.getMessage(), HttpStatus.OK);
        }


    }


    @PostMapping(value = "/leave")
    public ResponseEntity<?> addLeave(@RequestBody Leave leave) throws LeaveException {
        try{
            Leave newLeave = leaveService.addLeave(leave);
            return new ResponseEntity<>(newLeave, HttpStatus.CREATED);
        } catch(LeaveException e){
            System.err.println("Error adding leave: " + e.getMessage());

            // Specific error response, returning a string.
            String errorResponse = "Leave not added: " + e.getMessage();
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/leave/active")
    public List<Leave>  findAllLeaveByActiveFlag(@RequestBody boolean activeFlag) throws LeaveException {
        return leaveService.getAllLeavesByActiveStatus(activeFlag);
    }

    @GetMapping(value = "/manager/{managerId}/leaves")
    public List<Leave> findAllLeavesOfManagerEmployees(@PathVariable int managerId){
        return leaveService.findAllLeavesByManagerId(managerId);
    }


 }
