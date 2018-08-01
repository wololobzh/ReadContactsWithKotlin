package fr.acos.readcontactswithkotlin

import android.Manifest
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.app.ActivityCompat
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class MainActivity : AppCompatActivity(),AnkoLogger {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Envoi d'une requête de demande de permission pour READ_CONTACTS
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS), 14540)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        //Traitement effectuée pour la demande de permission ayant l'identifiant 14540
        if (requestCode == 14540)
        {
            //Test si l'utilisateur a donné sa permission pour la permission READ_CONTACT
            if (grantResults.size == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    )
            {
                //Appel de la fonction qui utilise le content provider
                consultationContacts()
            }
        }
    }

    /**
     * Fonction permettant d'afficher dans les logs
     * l'id, le nom, les numéros de téléphones et emails des contacts du téléphone.
     */
    fun consultationContacts()
    {
        //Liste des colonnes qui seront récupérées sur les contacts
        val projection = arrayOf<String>(
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)

        //Récupération de tout les contacts dans un cursor
        val contacts = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,projection,null,null,null)

        //Itération sur tout les contacts
        while (contacts.moveToNext())
        {
            //Récupération de l'identifiant du contact
            val contactId = contacts.getString(contacts.getColumnIndex(ContactsContract.Contacts.NAME_RAW_CONTACT_ID))
            //Récupération de son nom principale
            val contactNom = contacts.getString(contacts.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY))
            //Affichage dans les logs
            info("ID = $contactId ; Nom = $contactNom")


            //Récupération de tout les numéros de téléphone de tout les contacts bruts du contact non brut
            val telephones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null)
            //Itération sur tout les numéros de téléphone
            while (telephones.moveToNext())
            {
                //Récupération du numéro de téléphone
                val numero = telephones.getString(telephones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                //Affichage dans les logs
                info("Telephone = $numero")
            }
            telephones.close()

            //Récupération de tout les emails de tout les contacts bruts du contact non brut
            val emails = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId, null, null)
            //Itération sur tout les mails
            while (emails.moveToNext())
            {
                //Récupération du mail
                val mail = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA))
                //Affichage dans les logs
                info("EMail = $mail")
            }
            emails.close()
        }
        contacts.close()
    }
}
