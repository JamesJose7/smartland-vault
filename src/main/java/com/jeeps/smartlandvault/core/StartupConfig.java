package com.jeeps.smartlandvault.core;

import com.jeeps.smartlandvault.nosql.license_type.LicenseType;
import com.jeeps.smartlandvault.nosql.license_type.LicenseTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StartupConfig {

    @Autowired
    private LicenseTypeRepository licenseTypeRepository;

    @EventListener(ContextRefreshedEvent.class)
    public void bootConfiguration() {
        // Populate licenses
        saveLicense("Academic Free License v3.0","afl-3.0");
        saveLicense("Apache license 2.0","apache-2.0");
        saveLicense("Artistic license 2.0","artistic-2.0");
        saveLicense("Boost Software License 1.0","bsl-1.0");
        saveLicense("BSD 2-clause \"Simplified\" license","bsd-2-clause");
        saveLicense("BSD 3-clause \"New\" or \"Revised\" license","bsd-3-clause");
        saveLicense("BSD 3-clause Clear license","bsd-3-clause-clear");
        saveLicense("Creative Commons license family","cc");
        saveLicense("Creative Commons Zero v1.0 Universal","cc0-1.0");
        saveLicense("Creative Commons Attribution 4.0","cc-by-4.0");
        saveLicense("Creative Commons Attribution Share Alike 4.0","cc-by-sa-4.0");
        saveLicense("Educational Community License v2.0","ecl-2.0");
        saveLicense("Eclipse Public License 1.0","epl-1.0");
        saveLicense("Eclipse Public License 2.0","epl-2.0");
        saveLicense("European Union Public License 1.1","eupl-1.1");
        saveLicense("GNU Affero General Public License v3.0","agpl-3.0");
        saveLicense("GNU General Public License family","gpl");
        saveLicense("GNU General Public License v2.0","gpl-2.0");
        saveLicense("GNU General Public License v3.0","gpl-3.0");
        saveLicense("GNU Lesser General Public License family","lgpl");
        saveLicense("GNU Lesser General Public License v2.1","lgpl-2.1");
        saveLicense("GNU Lesser General Public License v3.0","lgpl-3.0");
        saveLicense("ISC","isc");
        saveLicense("LaTeX Project Public License v1.3c","lppl-1.3c");
        saveLicense("Microsoft Public License","ms-pl");
        saveLicense("MIT","mit");
        saveLicense("Mozilla Public License 2.0","mpl-2.0");
        saveLicense("Open Software License 3.0","osl-3.0");
        saveLicense("PostgreSQL License","postgresql");
        saveLicense("SIL Open Font License 1.1","ofl-1.1");
        saveLicense("University of Illinois/NCSA Open Source License","ncsa");
        saveLicense("The Unlicense","unlicense");
        saveLicense("zLib License","zlib");
        saveLicense("Ninguna","none");
    }

    private void saveLicense(String name, String id) {
        licenseTypeRepository.save(new LicenseType(id, name));
    }
}
