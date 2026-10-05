/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * $Id$ ServicioRegistroMock.java
 * Universidad de los Andes (Bogotá - Colombia)
 * Departamento de Ingeniería de Sistemas y Computación
 * Licenciado bajo el esquema Academic Free License version 3.0
 *
 * Ejercicio: Muebles de los Alpes
 * Autor: Juan Sebastián Urrego
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */

package co.edu.uniandes.csw.mueblesdelosalpes.logica.ejb;

import co.edu.uniandes.csw.mueblesdelosalpes.persistencia.mock.ServicioPersistenciaMock;
import co.edu.uniandes.csw.mueblesdelosalpes.dto.RegistroVenta;
import co.edu.uniandes.csw.mueblesdelosalpes.dto.Usuario;
import co.edu.uniandes.csw.mueblesdelosalpes.excepciones.OperacionInvalidaException;
import co.edu.uniandes.csw.mueblesdelosalpes.logica.interfaces.IServicioRegistroMockRemote;
import co.edu.uniandes.csw.mueblesdelosalpes.logica.interfaces.IServicioPersistenciaMockLocal;
import co.edu.uniandes.csw.mueblesdelosalpes.logica.interfaces.IServicioRegistroMockLocal;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;

/**
 * Implementación de los servicios de registro de un cliente en el sistema
 * @author Juan Sebastián Urrego
 */
@Stateless
public class ServicioRegistroMock implements IServicioRegistroMockRemote, IServicioRegistroMockLocal
{
    //-----------------------------------------------------------
    // Atributos
    //-----------------------------------------------------------
    
    /**
     * Interface con referencia al servicio de persistencia en el sistema
     */
    private IServicioPersistenciaMockLocal persistencia;

    //-----------------------------------------------------------
    // Constructor
    //-----------------------------------------------------------

    /**
     * Constructor de la clase sin argumentos
     */
     public ServicioRegistroMock()
     {
        persistencia=new ServicioPersistenciaMock();
     }

    //-----------------------------------------------------------
    // Métodos
    //-----------------------------------------------------------

    /**
     * Verifica y registra un usuario en el sistema
     * @param u Usuario a persistir
     */
    @Override
    public void registrar(Usuario u)throws OperacionInvalidaException
    {
        try
        {
            if(u.getDocumento()!=0)
            {

            if (u.getCompras() == null)
            {
                u.setCompras(new ArrayList<RegistroVenta>());
            }
            persistencia.create(u);
            }
            else
            {
                throw new OperacionInvalidaException("El número de documento no es válido");
            }
        }
        catch (OperacionInvalidaException ex)
        {
            throw new OperacionInvalidaException(ex.getMessage());
        }
    }

    /**
     * Elimina un cliente del sistema dado su login
     * @param login Login del cliente
     * @throws OperacionInvalidaException Excepción que es lanzada en caso de ocurrir un error
     */
    @Override
    public void eliminarCliente(String login) throws OperacionInvalidaException
    {
        Usuario cliente = (Usuario) persistencia.findById(Usuario.class, login);
        if (cliente != null && cliente.getCompras() != null && !cliente.getCompras().isEmpty())
        {
            throw new OperacionInvalidaException("El usuario ha realizado compras y por lo tanto no puede ser eliminado del sistema.");
        }
        try
        {
        Usuario u=(Usuario) persistencia.findById(Usuario.class, login);
        persistencia.delete(u);
        }
        catch(OperacionInvalidaException e)
        {
            throw new OperacionInvalidaException("Ocurrió un error al momento de eliminar");
        }
    }

    /**
     * Devuelve los clientes del sistema
     * @return clientes Lista con todos los clientes del sistema
     */
    @Override
    public List<Usuario> darClientes()
    {
        return(ArrayList<Usuario>) persistencia.findAll(Usuario.class);
    }

    /**
     * Actualiza los datos de un cliente registrado. Conserva su historial de compras
     * y, si no se envían, su contraseña y su tipo de usuario.
     * @param u Usuario con los datos nuevos
     * @throws OperacionInvalidaException Si el cliente no existe o el documento no es válido
     */
    @Override
    public void actualizarCliente(Usuario u) throws OperacionInvalidaException
    {
        Usuario actual = (Usuario) persistencia.findById(Usuario.class, u.getLogin());
        if (actual == null)
        {
            throw new OperacionInvalidaException("El usuario '" + u.getLogin() + "' no está registrado en el sistema");
        }
        if (u.getDocumento() == 0)
        {
            throw new OperacionInvalidaException("El número de documento no es válido");
        }
        if (u.getContraseña() == null)
        {
            u.setContraseña(actual.getContraseña());
        }
        if (u.getTipoUsuario() == null)
        {
            u.setTipoUsuario(actual.getTipoUsuario());
        }
        u.setCompras(actual.getCompras());
        persistencia.update(u);
    }

}
