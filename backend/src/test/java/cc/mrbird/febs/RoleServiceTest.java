package cc.mrbird.febs;

import cc.mrbird.febs.system.domain.Role;
import cc.mrbird.febs.system.service.RoleService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class RoleServiceTest extends AppTest{
    @Autowired
    private RoleService roleService;

    @Test
    public void testSelect1() {
        Role role = roleService.findByTeacher();
        System.out.println(role);
    }

    @Test
    public void testSelect2() {
        Role role = roleService.findByStudent();
        System.out.println(role);
    }
}
