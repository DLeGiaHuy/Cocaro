package CoCaro.sound;

import javax.sound.sampled.*;
/**
 * ==========================================================
 * Use Case ID   : UC-07
 * Use Case Name : Quản lý âm thanh
 * Actor         : Người chơi
 *
 * Chức năng:
 * Phát hiệu ứng âm thanh tương ứng với sự kiện trong game.
 *
 * Input:
 * - file : Tên file âm thanh cần phát.
 *
 * Xử lý:
 * 1. Nạp file âm thanh từ thư mục assets.
 * 2. Khởi tạo AudioInputStream.
 * 3. Tạo đối tượng Clip.
 * 4. Mở và phát âm thanh.
 *
 * Output:
 * Âm thanh được phát qua loa thiết bị.
 *
 * Mục đích:
 * Tăng trải nghiệm người dùng thông qua hiệu ứng âm thanh.
 * ==========================================================
 */
public class SoundManager {

    public static void play(String file){

        try{

            AudioInputStream audio =
                    AudioSystem.getAudioInputStream(
                            SoundManager.class.getResource(
                                    "/assets/" + file));

            Clip clip = AudioSystem.getClip();

            clip.open(audio);

            clip.start();

        }catch(Exception e){

            e.printStackTrace();
        }
    }
}