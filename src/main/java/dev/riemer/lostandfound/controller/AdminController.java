package dev.riemer.lostandfound.controller;

import dev.riemer.lostandfound.dto.LostItemAdminDto;
import dev.riemer.lostandfound.dto.UserInfoDto;
import dev.riemer.lostandfound.service.LostItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for Admin endpoints. Protected by the SecurityFilterChain and requires the ADMIN role
 */
@RequestMapping("/admin")
@RestController
public class AdminController {
    private final LostItemService lostItemService;

    /**
     * Constructor of the AdminController.
     *
     * @param lostItemService injected LostItemService
     */
    public AdminController(final LostItemService lostItemService) {
        this.lostItemService = lostItemService;
    }

    /**
     * Get all LostItems and their claims info.
     *
     * @return the LostItems with extra Claim info
     */
    @GetMapping("lost-item-claims")
    public ResponseEntity<List<LostItemAdminDto>> getLostItemClaims() {
        var lostItems = lostItemService.getAllLostItems();
        var lostItemDTOs = lostItems.stream()
                .map(LostItemAdminDto::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(lostItemDTOs);
    }

    /**
     * Upload endpoint to add new LostItems in bulk, supports multiple formats registered.
     *
     * @param file the upload MultipartFile
     * @return a simple string if successfully
     */
    @PostMapping("upload")
    public ResponseEntity<?> uploadLostItemsFile(final @RequestParam("file") MultipartFile file) throws IOException {
        lostItemService.processLostItemsFile(file);
        return new ResponseEntity<>("File processed successfully.", HttpStatus.OK);
    }

    /**
     * Retrieves fake user information for the given username.
     *
     * @param username the username to get information for
     * @return UserInfoDto containing fake user data
     */
    @GetMapping("get-user-info/{username}")
    public ResponseEntity<UserInfoDto> getUserInfo(final @PathVariable String username) {
        // Generate fake data for the user
        UserInfoDto userInfo = new UserInfoDto();
        userInfo.setUsername(username);
        userInfo.setFirstName("John");
        userInfo.setLastName("Doe");
        userInfo.setAddress("123 Main Street");
        userInfo.setCity("Leeuwarden");
        userInfo.setPostalCode("4422AD");
        userInfo.setCountry("NL");
        userInfo.setTelephone("+316 22334455");

        return ResponseEntity.ok(userInfo);
    }
}
