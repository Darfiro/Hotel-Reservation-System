package com.rsoi.loyalty.controller;

import com.rsoi.loyalty.model.UserLoyalty;
import com.rsoi.loyalty.service.DatabaseService;
import exceptions.NoContentException;
import exceptions.NotFoundException;
import loyaltyreqres.LoyaltyInfoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping(value = "/loyalty/*")
public class LoyaltyController
{

    @Autowired
    private DatabaseService db;

    /**
     * Gets information about loyalty program
     * @param userUid user unique identifier
     * @return OK with loyalty program status and discount is loyalty for user was found, NotFound otherwise
     * @throws NotFoundException
     */
    @GetMapping("{userUid}")
    public ResponseEntity<LoyaltyInfoResponse> getLoyaltyInfo(@PathVariable(name="userUid") String userUid)
                                                                throws NotFoundException
    {
        UserLoyalty loyalty = db.getLoyalty(userUid);
        if (loyalty == null)
            throw new NotFoundException("No loyalty for user with Uid: " + userUid);

        LoyaltyInfoResponse response = new LoyaltyInfoResponse(loyalty.getStatus().toString(), loyalty.getDiscount());
        return ResponseEntity.ok(response);
    }

    /**
     * Creates or updates loyalty
     * @param userUid user unique identifier
     * @throws NoContentException
     */
    @PostMapping("{userUid}")
    public void postLoyalty(@PathVariable(name="userUid") String userUid) throws NoContentException
    {
        db.updateUserLoyalty(userUid);
        throw new NoContentException("Loyalty updated for user " + userUid);
    }
}
