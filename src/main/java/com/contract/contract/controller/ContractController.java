package com.contract.contract.controller;

import com.contract.contract.model.Contract;
import com.contract.contract.service.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.contract.contract.constant.Constant.FILE_EXTENSION;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/contracts")
public class ContractController {
    public final ContractService contractService;

    @PostMapping
    public ResponseEntity<Contract> createContract(@RequestBody Contract contract){
        return ResponseEntity.created(URI.create("/contracts/userID")).body(contractService.createContract(contract));
    }

    @GetMapping
    public ResponseEntity<Page<Contract>> getContracts(@RequestParam(value = "page",defaultValue = "0") int page,
                                                      @RequestParam(value = "size", defaultValue = "10")int size){
        return ResponseEntity.ok().body(contractService.getAllContracts(page,size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contract> getContract(@PathVariable(value = "id") String id){
        return ResponseEntity.ok().body(contractService.getContact(id));
    }

    @PutMapping("/photo")
    public ResponseEntity<String> uploadPhoto(@RequestParam("id") String id, @RequestParam("file") MultipartFile file){
        return ResponseEntity.ok().body(contractService.uploadPhoto(id, file));
    }

    @GetMapping(path = "/image/{filename}",produces = {IMAGE_PNG_VALUE,IMAGE_JPEG_VALUE})
    public byte[] getPhoto(@PathVariable("filename") String filename) throws IOException{
        return Files.readAllBytes(Paths.get(FILE_EXTENSION+filename));
    }

//    @DeleteMapping(path = "/delete/{id}")
//    public void deleteUser(@RequestBody Contract contract){
//        return ResponseEntity.ok().body(contractService.deleteContract(contract));
//    }

}
