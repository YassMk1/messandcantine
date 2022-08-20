package be.mkfin.messandcantine.service.impl;

import be.mkfin.messandcantine.entity.Image;
import be.mkfin.messandcantine.repository.ImageRepository;
import be.mkfin.messandcantine.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ImageServiceImpl implements ImageService {
    @Autowired
    private ImageRepository imageRepository ;

    @Override
    public Image store(Image image) {
        return imageRepository.save(image);
    }
}
