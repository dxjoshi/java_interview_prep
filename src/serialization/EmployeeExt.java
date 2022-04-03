package serialization;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class EmployeeExt implements Externalizable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;

    public EmployeeExt(){}  //This constructor is called during deSerializaition process, as we have implemented Externalizable. 

    public EmployeeExt(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return "EmployeeExt [id=" + id + ", name=" + name + "]";
    }

    @Override
    public void writeExternal(ObjectOutput oo) throws IOException {
        System.out.println("in writeExternal()");
        oo.writeInt(id);
        oo.writeObject(name);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException,  ClassNotFoundException {
        System.out.println("in readExternal()");
        this.id=in.readInt();
        this.name=(String)in.readObject();
    }

}
