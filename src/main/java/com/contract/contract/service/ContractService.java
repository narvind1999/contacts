package com.contract.contract.service;

import com.contract.contract.model.Contract;
import com.contract.contract.repository.ContractRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.contract.contract.constant.Constant.FILE_EXTENSION;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
@Slf4j
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class ContractService {

    private final ContractRepository contractRepository;

    public Page<Contract> getAllContracts(int page, int size){
        return contractRepository.findAll(PageRequest.of(page,size, Sort.by("name")));
    }

    public Contract getContact(String id){
        return contractRepository.findById(id).orElseThrow(()-> new RuntimeException("Contract not found"));
    }

    public Contract createContract(Contract contract){
        return contractRepository.save(contract);
    }

    public void deleteContract(Contract contract){
        contractRepository.delete(contract);
    }

    public String uploadPhoto(String id, MultipartFile file){
        //log.info("Saving the user picture with ID: {}",id);
        Contract contract = getContact(id);
        String photoUrl = photoFunction.apply(id,file);
        contract.setPhotoUrl(photoUrl);
        contractRepository.save(contract);
        return photoUrl;
    }


    public final Function<String,String> fileExtension = filename -> Optional.of(filename).filter(name ->name.contains("."))
            .map(name -> "." + name.substring(filename.lastIndexOf(".") + 1)).orElse(".png");
    public final BiFunction<String, MultipartFile, String > photoFunction = (id,image)->{
        try{
            Path fileStorageLocation = Paths.get(FILE_EXTENSION).toAbsolutePath().normalize();
            if(!Files.exists(fileStorageLocation)){
                //log.info("inside if statement");
                Files.createDirectories(fileStorageLocation);
            }
            Files.copy(image.getInputStream(),fileStorageLocation.resolve(id+fileExtension.apply(image.getOriginalFilename())),REPLACE_EXISTING);
            return ServletUriComponentsBuilder.fromCurrentContextPath().path("/contracts/image/"+id+fileExtension
                    .apply(image.getOriginalFilename())).toUriString();
        }catch (Exception e){
            throw new RuntimeException("Unable to save the image");
        }
    };
}
