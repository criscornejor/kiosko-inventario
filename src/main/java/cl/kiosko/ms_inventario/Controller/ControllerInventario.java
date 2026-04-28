package cl.kiosko.ms_inventario.Controller;

import cl.kiosko.ms_inventario.Service.InventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/kiosko/inventario")
public class ControllerInventario {
    @Autowired
    InventarioService inventarioService;


}
