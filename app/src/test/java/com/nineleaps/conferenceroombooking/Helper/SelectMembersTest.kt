package com.nineleaps.conferenceroombooking.Helper

import android.app.Application
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nhaarman.mockitokotlin2.*
import com.nineleaps.conferenceroombooking.model.EmployeeList
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations

@RunWith(JUnit4::class)
class SelectMembersTest {

    @Mock
    lateinit var context: Application

    @Mock
    lateinit var NameTextView: TextView

    @Mock
    lateinit var EmailTextView: TextView

    var employeeList= ArrayList<EmployeeList>()

    lateinit var selectMembersAdapter: SelectMembers

    @Mock
    lateinit var listener: SelectMembers.ItemClickListener

    @Captor
    lateinit var notifyCaptor: ArgumentCaptor<RecyclerView>

    val employee = EmployeeList()
    @Before
    fun setUp() {

        MockitoAnnotations.initMocks(this)
//        listener = mock(SelectMembers.mClickListener!!::class.java)
        selectMembersAdapter = SelectMembers(employeeList, listener)
    }

    @Test
    fun testGetItemCount() {
        doReturn(0).`when`(spy(selectMembersAdapter)).itemCount
    }

   @Test
   fun testViewHolder(){
        val employee = EmployeeList()
       employee.name = "sdv"
       employee.email = "sdv"
       employeeList.add(employee)
       val viewHolder = mock(SelectMembers.ViewHolder::class.java)
       viewHolder.nameTextView = NameTextView
       viewHolder.emailTextView = EmailTextView
       selectMembersAdapter.onBindViewHolder(viewHolder, 0)
       verify(viewHolder.nameTextView, times(1)).text = "sdv"
   }
}