package com.dvlcube.app.rest;

import com.dvlcube.app.interfaces.MenuItem;
import com.dvlcube.app.jpa.repo.JobRepository;
import com.dvlcube.app.manager.data.JobBean;
import com.dvlcube.app.manager.data.vo.MxRestResponse;
import com.dvlcube.utils.interfaces.rest.MxFilterableBeanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;
import java.util.Optional;

import static com.dvlcube.app.manager.data.e.Menu.CONFIGURATION;

@RestController
@MenuItem(value = CONFIGURATION)
@RequestMapping("${dvl.rest.prefix}/job")
public class JobService implements MxFilterableBeanService<JobBean, Long> {

    private final JobRepository repository;

    public JobService(@Autowired JobRepository repository) {
        this.repository = repository;
    }

    @Override
    @GetMapping
    public Iterable<JobBean> get(@RequestParam Map<String, String> params) {
        return repository.findAllBy(params);
    }

    @Override
    @GetMapping("/{id}")
    public Optional<JobBean> get(@RequestParam Long id) {
        return repository.findById(id);
    }

    @Override
    @PostMapping
    public MxRestResponse post(@Valid @RequestParam JobBean body) {
        JobBean save = repository.save(body);
        return GenericRestResponse.ok(save.getId());
    }

    @GetMapping("/like")
    public Iterable<JobBean> getLike(@RequestParam String name) {
        return repository.findByNameLike(name);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }

}
