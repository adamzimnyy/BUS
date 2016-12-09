import elf.ElfFile;
import elf.ElfSectionHeader;
import elf.MemoizedObject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import org.apache.commons.lang.ArrayUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    String path = "F:/BSU/elf/patch_me_bin";
    static File file;

    ElfFile elf;

    public Controller() throws IOException {
    }

    @FXML
    TextField pathField, addressField, bytesField;

    @FXML
    Button readAddressButton, readFileButton, browseButton, writeBytesButton;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        pathField.setText(path);

        writeBytesButton.setOnAction(this::writeBytes);
        browseButton.setOnAction(this::browse);
        readFileButton.setOnAction(this::readFile);

        readAddressButton.setOnAction(this::readAddress);
    }

    private void readAddress(ActionEvent actionEvent) {
        String addressString = addressField.getText();
        int address;
        if (addressString.startsWith("0x"))
            address = Integer.parseInt(addressString.replace("0x", ""), 16);
        else
            address = Integer.parseInt(addressString);
        ElfSectionHeader section = findSection(address);
        try {
            System.out.println(section.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }

        long writeOffsetBytes = address - section.address+section.section_offset;
        String bytesToWrite[] = bytesField.getText().split(" ");

        List<Byte> bytes = new ArrayList<>();
        for (String s : bytesToWrite) {
            String[] parts = s.replace("0x", "").split("(?<=\\G.{2})");
            for (int i = parts.length - 1; i >= 0; i--) {
                bytes.add((byte) Integer.parseInt(parts[i], 16));
            }
        }
        Byte[] bytesA = bytes.toArray(new Byte[bytes.size()]);
        byte[] bytesA2 = ArrayUtils.toPrimitive(bytesA);
        RandomAccessFile raf = null;

        try {
            raf = new RandomAccessFile(file, "rw");
            System.out.println("offset: " + writeOffsetBytes);
            System.out.println("bytes len: " + bytesA2.length);
            raf.seek(writeOffsetBytes);
            byte[] read = new byte[bytesA2.length];
            for (int i = 0; i < bytesA2.length; i++) {
                read[i] = raf.readByte();
            }
            System.out.println("Address is "+printArray(read));

            raf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeBytes(ActionEvent event) {

        String addressString = addressField.getText();
        int address;
        if (addressString.startsWith("0x"))
            address = Integer.parseInt(addressString.replace("0x", ""), 16);
        else
            address = Integer.parseInt(addressString);
        ElfSectionHeader section = findSection(address);
        try {
            System.out.println(section.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        long writeOffsetBytes = address - section.address+section.section_offset;
        String bytesToWrite[] = bytesField.getText().split(" ");
        int count = 0;
        List<Byte> bytes = new ArrayList<>();
        for (String s : bytesToWrite) {
            String[] parts = s.replace("0x", "").split("(?<=\\G.{2})");
            for (int i = parts.length - 1; i >= 0; i--) {
                bytes.add((byte) Integer.parseInt(parts[i], 16));
            }
        }
        Byte[] bytesA = bytes.toArray(new Byte[bytes.size()]);
        byte[] bytesA2 = ArrayUtils.toPrimitive(bytesA);
        RandomAccessFile raf = null;

        try {
            raf = new RandomAccessFile(file, "rw");
            System.out.println("offset: " + writeOffsetBytes);
            System.out.println("bytes len: " + bytesA2.length);
            raf.seek(writeOffsetBytes);
            byte[] read = new byte[bytesA2.length];
            for (int i = 0; i < bytesA2.length; i++) {
                read[i] = raf.readByte();
            }
            System.out.println("Replacing " + printArray(read));
            raf.seek(writeOffsetBytes);
            raf.write(bytesA2, 0, bytesA2.length);
            raf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Writing   " + printArray(bytes.toArray()));

    }

    private static String printArray(byte[] a) {
        if (a == null)
            return "null";

        int iMax = a.length - 1;
        if (iMax == -1)
            return "[]";

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            b.append(String.format("%02X ", (byte) a[i]));
            if (i == iMax)
                return b.append(']').toString();
            b.append(", ");
        }
    }


    public static String printArray(Object[] a) {
        if (a == null)
            return "null";

        int iMax = a.length - 1;
        if (iMax == -1)
            return "[]";

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            b.append(String.format("%02X ", (byte) a[i]));
            if (i == iMax)
                return b.append(']').toString();
            b.append(", ");
        }
    }

    private ElfSectionHeader findSection(int address) {
        for (MemoizedObject<ElfSectionHeader> s : elf.sectionHeaders) {
            try {
                ElfSectionHeader header = s.getValue();
                if (address >= header.address && address < header.address + header.size) {
                    System.out.println(Integer.toHexString(address) + " " + Long.toHexString(header.address) + " " + Long.toHexString(header.section_offset));
                    return header;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void browse(ActionEvent actionEvent) {
        FileChooser chooser = new FileChooser();
        File dir = new File(pathField.getText().isEmpty() ? path : pathField.getText());
        if (dir.exists() && dir.isDirectory())
            chooser.setInitialDirectory(dir);
        chooser.setTitle("Open File");
        try {
            file = chooser.showOpenDialog(null);
        } catch (NullPointerException e) {
            pathField.setText("");
        }
    }

    private void readFile(ActionEvent actionEvent) {
        if (file == null) {
            file = new File(path);
        }
        try {
            elf = ElfFile.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
