package com.dvlcube.app.rest;

import com.dvlcube.app.interfaces.MenuItem;
import com.dvlcube.utils.aspects.stats.Stat;
import com.dvlcube.utils.aspects.stats.Stats;
import com.dvlcube.utils.interfaces.MxService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.dvlcube.app.manager.data.e.Menu.MONITORING;
import static com.dvlcube.utils.query.MxQuery.$;

/**
 * Application performance stats.
 * 
 * @since 17 de abr de 2019
 * @author Ulisses Lima
 */
@RestController
@MenuItem(value = MONITORING, readOnly = true)
@RequestMapping("${dvl.rest.prefix}/stats")
public class StatService implements MxService {
	private Logger log = LogManager.getLogger(this.getClass());

	/**
	 * @param params
	 * @return stats
	 * @since 17 de abr de 2019
	 * @author Ulisses Lima
	 */
	@GetMapping
	public List<Stat> get(@RequestParam Map<String, String> params) {

		Comparator<Stat> compTotal = (a, p ) -> a.getTotal().compareTo(p.getTotal());
		Comparator<Stat> compAvg = (a, p ) -> a.avg().compareTo(p.avg());

		return
				Stats.values()
						.stream()
						.sorted(compTotal.reversed())
						.sorted(compAvg.reversed())
						.collect(Collectors.toList());
	}

	/**
	 * @param params
	 * @return stats
	 * @since 17 de abr de 2019
	 * @author Ulisses Lima
	 */
	@DeleteMapping
	public List<Stat> delete(@RequestParam Map<String, String> params) {
		Stats.consume(item -> log.info("removing {}", item));
		return get(params);
	}

	/**
	 * @param id
	 * @return stat by action
	 * @since 17 de abr de 2019
	 * @author Ulisses Lima
	 */
	@GetMapping("/{id}")
	public Stat get(@PathVariable String id) {
		return $(Stats.values()).filterOne(stat -> stat.getAction().equals(id));
	}

	/**
	 * @param regex expression
	 * @return stats that match the regex
	 * @since 17 de abr de 2019
	 * @author Ulisses Lima
	 */
	@GetMapping("/matching/{regex}")
	public Collection<Stat> getMatching(@PathVariable String regex) {
		return $(Stats.values()).filter(stat -> stat.getAction().matches(regex)).o;
	}

	/**
	 * @param params
	 * @param id
	 * @return Iterable<Stat> stats like id, fallback to GET params
	 * @since 2 de mai de 2019
	 * @author Ulisses Lima
	 */
	@GetMapping("/like")
	public Iterable<Stat> getLike(@RequestParam Map<String, String> params, @RequestParam(required = true) String id) {
		if ($(id).isBlank())
			return get(params);

		return $(Stats.values()).filter(stat -> stat.getAction().toLowerCase().contains(id.toLowerCase())).o;
	}
}
